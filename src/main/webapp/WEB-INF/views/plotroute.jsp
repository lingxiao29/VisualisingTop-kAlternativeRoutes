<%--
  Created by IntelliJ IDEA.
  User: llx
  Date: 18/02/2018
  Time: 9:48 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Alternative Routes Demo</title>
    <head>
        <style>
            /* Always set the map height explicitly to define the size of the div
             * element that contains the map. */
            #map {
                height: 100%;
            }

            /* Optional: Makes the sample page fill the window. */
            html, body {
                height: 100%;
                margin: 0;
                padding: 0;
            }
        </style>
    </head>
</head>

<body>
<table>
    <tr>
        <td>Method:</td>
        <td>${method}</td>
        <td>Top-k:</td>
        <td>${topK}</td>
       <!-- <td>Source:</td>
        <td>${source}</td>
        <td>Target:</td>
        <td>${target}</td> -->
    </tr>
</table>


<div id="map"></div>

<script type="text/javascript">

    var getLatLng = function(lat, lng) {
        return new google.maps.LatLng(lat, lng);
    };

    function initMap() {


        // Define the overlay, derived from google.maps.OverlayView
        function Label(opt_options) {
            // Initialization
            this.setValues(opt_options);

            // Label specific
            var span = this.span_ = document.createElement('span');
            span.style.cssText = 'position: relative; left: -50%; top: -8px; ' +
                'white-space: nowrap; border: 1px solid blue; ' +
                'padding: 2px; background-color: white';

            var div = this.div_ = document.createElement('div');
            div.appendChild(span);
            div.style.cssText = 'position: absolute; display: none';
        }
        Label.prototype = new google.maps.OverlayView();

        // Implement onAdd
        Label.prototype.onAdd = function() {
            var pane = this.getPanes().floatPane;
            pane.appendChild(this.div_);

            // Ensures the label is redrawn if the text or position is changed.
            var me = this;
            this.listeners_ = [
                google.maps.event.addListener(this, 'position_changed',
                    function() { me.draw(); }),
                google.maps.event.addListener(this, 'text_changed',
                    function() { me.draw(); })
            ];
        };

        // Implement onRemove
        Label.prototype.onRemove = function() {
            var i, I;
            this.div_.parentNode.removeChild(this.div_);

            // Label is removed from the map, stop updating its position/text.
            for (i = 0, I = this.listeners_.length; i < I; ++i) {
                google.maps.event.removeListener(this.listeners_[i]);
            }
        };

        // Implement draw
        Label.prototype.draw = function() {
            var projection = this.getProjection();
            var position = projection.fromLatLngToDivPixel(this.get('position'));

            var div = this.div_;
            div.style.left = position.x + 'px';
            div.style.top = position.y + 'px';
            div.style.display = 'block';

            this.span_.innerHTML = this.get('text').toString();
        };



        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            center: {lat: 39.5501, lng: -105.7821},
            mapTypeId: 'roadmap'
        });

        /**
         * Set up a boundaries for Colorado area
         */
        var bounds = [
            {lat: 41.00727, lng: -109.050731},
            {lat: 41.00727, lng: -107.050731},
            {lat: 41.00727, lng: -105.050731},
            {lat: 41.00727, lng: -103.050731},
            {lat: 41.00727, lng: -102.054698},
            {lat: 41.00727, lng: -102.054698},
            {lat: 37.000329, lng: -102.054698},
            {lat: 37.000329, lng: -102.054698},
            {lat: 37.000329, lng: -103.503952},
            {lat: 37.000329, lng: -105.503952},
            {lat: 37.000329, lng: -107.503952},
            {lat: 37.000329, lng: -109.050731},
            {lat: 37.010607, lng: -109.050731},
            {lat: 41.00727, lng: -109.050731}
        ];
        var boundary = new google.maps.Polyline({
            path: bounds,
            geodesic: true,
            strokeColor: '#ff5e44',
            strokeOpacity: 0.8,
            strokeWeight: 4,
            map: map
        });

        var sourceLatLng = "${source}".split("_");
        var targetLatLng = "${target}".split("_");
        var colors = ['#4169E1', '#006400', '#FF00FF', '#ff0033', '#FFA500', '#4B0082'];

        if ("${method}" == "google") {
            var directionsService = new google.maps.DirectionsService;
            directionsService.route({
                origin: getLatLng(sourceLatLng[0], sourceLatLng[1]),
                destination: getLatLng(targetLatLng[0], targetLatLng[1]),
                provideRouteAlternatives: true,
                travelMode: 'DRIVING'
            }, function(response, status) {
                if (status == google.maps.DirectionsStatus.OK) {
                    for (var i = 0, len = response.routes.length; i < len; i++) {
                        new google.maps.DirectionsRenderer({
                            map: map,
                            directions: response,
                            routeIndex: i,
                            polylineOptions: {strokeColor: colors[i], strokeWeight: 4}
                        });
                    }
                    //directionsDisplay.setDirections(response);
                } else {
                    window.alert('Directions request failed due to ' + status);
                }
            });
        } else {
            /**
             * Plot Source Marker
             */
            var marker = new google.maps.Marker({
                position: getLatLng(sourceLatLng[0], sourceLatLng[1]),
                map: map,
                title: 'Source'
            });
            /**
             * Plot Source Marker
             */
            var marker = new google.maps.Marker({
                position: getLatLng(targetLatLng[0], targetLatLng[1]),
                map: map,
                title: 'Target'
            });

            var pathArray = new Array();
            <c:forEach items = "${multiPaths}" var = "sp">
                var path = new Array();

                <c:forEach items = "${sp}" var = "add">
                    var myLocation =  new google.maps.LatLng(${add.lat}, ${add.lng});
                    path.push(myLocation);
                </c:forEach>

                pathArray.push(path);
            </c:forEach>


            var lenArray = new Array();
            <c:forEach items = "${labelList}" var = "ll">
                lenArray.push(${ll})
            </c:forEach>

            var cur = 0;
            var renderArray = [];

            if (pathArray.length > 0) {
                plotPolyLine(pathArray[cur]);
            }

            function plotPolyLine(singlePath) {
                renderArray[cur] = new google.maps.Polyline({
                    path: singlePath,
                    geodesic: true,
                    strokeColor: colors[cur],
                    strokeOpacity: 0.8,
                    strokeWeight: 4});
                renderArray[cur].setMap(map);

                var key = Math.floor(singlePath.length / ((cur + 1) * 2));
                addLabel(lenArray[cur], singlePath[key]);

                cur++;
                if (cur < pathArray.length) {
                    plotPolyLine(pathArray[cur]);
                }
            };


            function addLabel(lenNum, pos) {
                // create an invisible marker
                var labelMarker = new google.maps.Marker({
                    position: pos,
                    map: map,
                    visible: false
                });

                var myLabel = new Label({
                    map: map,
                    text: lenNum + ' km'
                });
                myLabel.bindTo('position', labelMarker, 'position')
            }

        }

    }

</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key&callback=initMap">
</script>

</body>

</html>
