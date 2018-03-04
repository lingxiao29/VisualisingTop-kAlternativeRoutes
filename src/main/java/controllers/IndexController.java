package controllers;

import models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by llx on 18/02/2018.
 */

@Controller
public class IndexController {

    private final EdgeWeightedGraph G;
    private final String[] coo = new String[435667];

    public IndexController() {
        G = new EdgeWeightedGraph();
        G.getCoordinate(coo);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showForm() {
        String[] topKList = {"2", "3", "4", "5"};
        return new ModelAndView("index", "parameter", new Parameter(topKList));
    }

    @RequestMapping(value = "/plotroute", method = RequestMethod.POST)
    public String plotRoutes(@ModelAttribute("parameter") Parameter parameter, ModelMap model) {
        model.addAttribute("method", parameter.getMethod());
        model.addAttribute("topK", parameter.getTopK());
        model.addAttribute("source", parameter.getSource());
        model.addAttribute("target", parameter.getTarget());

        List<List<Position>> multiRoutes = new ArrayList<List<Position>>();
        List<Integer> labelList = new ArrayList<Integer>();

        int s = G.getNearestNode(coo, parameter.getSource());
        int t = G.getNearestNode(coo, parameter.getTarget());

        if (parameter.getMethod().equals("plateau")) {

            DijkstraUndirectedSP fspt = new DijkstraUndirectedSP(G, s);
            DijkstraUndirectedSP bspt = new DijkstraUndirectedSP(G, t);
            Plateau pl = new Plateau(s, t, G, fspt, bspt);

            for (Map.Entry<Integer, List<Integer>> entry : pl.getPaths(parameter.getTopK()).entrySet()) {
                labelList.add(entry.getKey());
                List<Position> positions = new ArrayList<Position>();
                for (int i : entry.getValue()) {
                    String[] fields = coo[i].split("_");
                    Position p = new Position(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]));
                    positions.add(p);
                }
                multiRoutes.add(positions);
            }
        } else if (parameter.getMethod().equals("viapath")) {
            DijkstraUndirectedSP sp = new DijkstraUndirectedSP(G, s);
            double spLen = sp.distTo(t);
            BiDijkstraUndirectedSp bsp = new BiDijkstraUndirectedSp(G, s, t, spLen);

            for (Map.Entry<Integer, List<Integer>> entry : bsp.getAlternativePaths(parameter.getTopK()).entrySet()) {
                labelList.add(entry.getKey());
                List<Position> positions = new ArrayList<Position>();
                for (int i :  entry.getValue()) {
                    String[] fields = coo[i].split("_");
                    Position p = new Position(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]));
                    positions.add(p);
                }
                multiRoutes.add(positions);
            }

        } else {

        }

        model.addAttribute("source", coo[s]);
        model.addAttribute("target", coo[t]);
        model.addAttribute("labelList", labelList);
        model.addAttribute("multiPaths", multiRoutes);
        return "plotroute";
    }
}
