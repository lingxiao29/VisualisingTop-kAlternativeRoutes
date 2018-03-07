<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
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
        form {
            margin: -4 auto;
            text-align: center;
        }
        #floating-panel {
            height: 16px;
            position: absolute;
            top: 10px;
            left: 10%;
            z-index: 5;
            background-color: #66b3ff;
            padding: 5px;
            border: 1px solid #66b3ff;
            text-align: center;
            font-family: 'Roboto','sans-serif';
            line-height: 15px;
            padding-left: 10px;
        }
    </style>
</head>
<body>
<!--<h1 style="color:blue;"><b>Alternative Routes Demo</b></h1>-->
    <div id="floating-panel">
    <form:form method="POST" action="/routeDemo-1.0-SNAPSHOT/plotroute" modelAttribute="parameter" target="_blank">
        <table>
            <tr>
                <td style="padding-right:10px">
                    <form:label path="Method"><b>Method:</b></form:label>
                    <form:radiobutton id="plateau" path="method" value="plateau" checked="checked"/><a href="http://www.camvit.com/camvit-technical-english/Camvit-Choice-Routing-Explanation-english.pdf" target="_blank">Plateau</a>
                    <form:radiobutton id="viapath" path="method" value="viapath"/><a href="https://www.microsoft.com/en-us/research/wp-content/uploads/2010/01/alternativeSea2010.pdf" target="_blank">Via Path</a>
                    <form:radiobutton id="google" path="method" value="google"/>Google Map
                </td>
                <td style="padding-right:10px">
                    <form:label path="topK"><b>Top-k:</b> </form:label>
                    <form:select path="topK">
                        <form:option id="selectK" selected="false" value="1" />
                        <form:options items="${parameter.topkList}"/>
                    </form:select>
                </td>
                <td style="padding-right:10px" hidden>
                    <form:label path="Source"><b style="color:green">Source:</b> </form:label>
                    <form:input id= "source" path="source"/>
                </td>
                <td style="padding-right:10px" hidden>
                    <form:label path="Target"><b style="color:purple">Target:</b> </form:label>
                    <form:input id= "target" path="target"/>
                </td>
                <td><input type="submit" value="Submit"/></td>
                <td><input onclick="clearPage()" type=button value=" Clear "></td>
            </tr>
        </table>
    </form:form>
    </div>

    <div id="map"></div>

    <script>

        var bounds = {
            north: 41.0,
            south: 37.0,
            east: -102,
            west: -109
        };

        /**
         * Create new map
         */
        var map;
        var myMapOptions = {
            zoom: 8,
            center: {lat: 39.5501, lng: -105.7821},
            mapTypeId: 'roadmap'
        };

        /**
         * Global marker object that holds all markers.
         * @type {Object.<string, google.maps.LatLng>}
         */
            //var markers = []; // array
        var markers = {}; // object


        /**
         * Concatenates given lat and lng with an underscore and returns it.
         * This id will be used as a key of marker to cache the marker in markers object.
         * @param {!number} lat Latitude.
         * @param {!number} lng Longitude.
         * @return {string} Concatenated marker id.
         */
        var getMarkerUniqueId= function(lat, lng) {
            return lat + '_' + lng;
        }

        /**
         * Creates an instance of google.maps.LatLng by given lat and lng values and returns it.
         * This function can be useful for getting new coordinates quickly.
         * @param {!number} lat Latitude.
         * @param {!number} lng Longitude.
         * @return {google.maps.LatLng} An instance of google.maps.LatLng object
         */
        var getLatLng = function(lat, lng) {
            return new google.maps.LatLng(lat, lng);
        };


        function initMap() {
            map = new google.maps.Map(document.getElementById('map'), myMapOptions);


            /**
             * Set up a boundaries for Colorado area
             */
            var boundaries = [
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
                path: boundaries,
                geodesic: true,
                strokeColor: '#ff5e44',
                strokeOpacity: 0.8,
                strokeWeight: 4,
            });
            boundary.setMap(map);


            // This event listener will call addMarker() when the map is clicked.
            map.addListener('click', function (event) {
                if (Object.keys(markers).length < 2) {
                    var myLat = event.latLng.lat();
                    var myLng = event.latLng.lng();
                    if (myLat > bounds.south && myLat < bounds.north && myLng > bounds.west && myLng < bounds.east) {
                        addMarker(event.latLng);
                        if (document.getElementById("source").value == "")
                            document.getElementById("source").value=myLat + "_" + myLng;
                        else if (document.getElementById("source").value != "" && document.getElementById("target").value == "")
                            document.getElementById("target").value=myLat + "_" + myLng;
                        else if (document.getElementById("source").value == "" && document.getElementById("target").value != "")
                            document.getElementById("source").value=myLat + "_" + myLng;
                    } else {
                        alert("Please pice the node inside the Colorado area.")
                    }
                } else {
                    alert("Only allow to pick one source and one target node.");
                }
            });
        }


        // Adds a marker to the map and push to the array or store it in markers object.
        function addMarker(location) {
            var lat = location.lat(); // lat of clicked point
            var lng = location.lng(); // lng of clicked point
            var markerId = getMarkerUniqueId(lat, lng); // cache this marker in markers object
            //console.log(lat + ", " + lng);
            var marker = new google.maps.Marker({
                position: location,
                map: map,
                id: 'marker_' + markerId
            });
            //markers.push(marker);  // store this marker in the array
            markers[markerId] = marker;  // store this marker in the marker object
            bindMarkerEvents(marker); // bind right click event to marker
        }

        /**
         * Binds right click event to given marker and invokes a callback function that will remove the marker from map.
         * @param {!google.maps.Marker} marker A google.maps.Marker instance that the handler will binded.
         */
        var bindMarkerEvents = function(marker) {
            google.maps.event.addListener(marker, "rightclick", function (point) {
                var markerId = getMarkerUniqueId(point.latLng.lat(), point.latLng.lng()); // get marker id by using clicked point's coordinate
                var marker = markers[markerId]; // find marker
                removeMarker(marker, markerId); // remove it
            });
        };

        /**
         * Removes given marker from map.
         * @param {!google.maps.Marker} marker A google.maps.Marker instance that will be removed.
         * @param {!string} markerId Id of marker.
         */
        var removeMarker = function(marker, markerId) {
            marker.setMap(null); // set markers setMap to null to remove it from map
            var deleteId = marker.getPosition().lat() + "_" + marker.getPosition().lng();
            if (deleteId == document.getElementById("source").value)
                document.getElementById("source").value = "";
            else
                document.getElementById("target").value = "";
            delete markers[markerId]; // delete marker instance from markers object
        };

        /**
         * Removes the markers from the map.
         */
        function clearPage() {
            document.getElementById("source").value = "";
            document.getElementById("target").value = "";
            document.getElementById("plateau").checked="checked";
            document.getElementById("selectK").selected="true";
            for (var m in markers) {
                markers[m].setMap(null);
                delete markers[m];
            }
        }

    </script>
    <script async defer
            src="https://maps.googleapis.com/maps/api/js?key&callback=initMap">
    </script>
</body>
</html>
