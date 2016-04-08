//package com.mishu.cgwy.order;
//
//import jsprit.analysis.toolbox.Plotter;
//import jsprit.core.algorithm.VehicleRoutingAlgorithm;
//import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
//import jsprit.core.problem.Location;
//import jsprit.core.problem.VehicleRoutingProblem;
//import jsprit.core.problem.job.Service;
//import jsprit.core.problem.vehicle.VehicleImpl;
//import jsprit.core.problem.vehicle.VehicleType;
//import jsprit.core.problem.vehicle.VehicleTypeImpl;
//import jsprit.core.reporting.SolutionPrinter;
//import jsprit.core.util.Coordinate;
//import jsprit.core.util.Solutions;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.Arrays;
//import java.util.Collection;
//
//public class OrderTest {
//    @Test
//    public void test() throws IOException, InvalidFormatException {
//        try {
//            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
//            Class.forName("net.sf.log4jdbc.DriverSpy");
//
//            Connection con =
//                    DriverManager.getConnection("jdbc:log4jdbc:mysql://115.28.64.174/cgwy?useUnicode=true&amp;characterEncoding=utf-8&amp;zeroDateTimeBehavior=convertToNull", "root", "cgwy1902") ;
//            Statement stmt = con.createStatement() ;
//            ResultSet rs = stmt.executeQuery("select o.restaurant_id,c.block_id,r.longitude,r.latitude,20 as weight from cgwy_order o,customer c,restaurant r where r.customer_id=c.id and o.customer_id=c.id and o.status =4 and submit_date>'2015-08-13' and submit_date<'2015-08-14' and c.block_id=1");
//
//            int count = 0;
//            while (rs.next()) {
//
//                Long resId = (Long)rs.getObject("restaurant_id");
//                Double longitude = (Double)rs.getObject("longitude");
//                Double latitude = (Double)rs.getObject("latitude");
//                Long weight = (Long)rs.getObject("weight");
//
//                if (resId != null && longitude != null && latitude != null && weight != null) {
//                    Service.Builder sBuilder1 = Service.Builder.newInstance(String.valueOf(count++)).addSizeDimension(0, weight.intValue());
//                    sBuilder1.setLocation(Location.newInstance(longitude, latitude));
//                    Service service1 = sBuilder1.build();
//                    vrpBuilder.addJob(service1);
//                }
//            }
//
//
////        new VrpXMLReader(vrpBuilder).read("problem.xml");
//
//            int nuOfVehicles = 20;
//            int capacity = 800;
//            Coordinate firstDepotCoord = Coordinate.newInstance(116.301355, 40.101645);
////        Coordinate fourth = Coordinate.newInstance(50, 50);
//
//            int depotCounter = 1;
//            for (Coordinate depotCoord : Arrays.asList(firstDepotCoord)) {
//                for (int i = 0; i < nuOfVehicles; i++) {
//                    String typeId = depotCounter + "_type";
//                    VehicleType vehicleType = VehicleTypeImpl.Builder.newInstance(typeId).addCapacityDimension(0, capacity).setCostPerDistance(1.0).build();
//                    String vehicleId = depotCounter + "_" + (i + 1) + "_vehicle";
//                    VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicleId);
//                    vehicleBuilder.setStartLocation(Location.newInstance(depotCoord.getX(), depotCoord.getY()));  //defines the location of the vehicle and thus the depot
//                    vehicleBuilder.setType(vehicleType);
//                    VehicleImpl vehicle = vehicleBuilder.build();
//                    vrpBuilder.addVehicle(vehicle);
//                }
//                depotCounter++;
//            }
//
//            vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
//            VehicleRoutingProblem vrp = vrpBuilder.build();
//
//            new Plotter(vrp).plot("problem.png", "p01");
//
//            VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "algo.xml");
//            Collection solutions = vra.searchSolutions();
//
//            System.out.println(solutions.size());
//
//            Plotter plotter = new Plotter(vrp, Solutions.bestOf(solutions));
//            plotter.plot("solution.png", "p01");
//
//            SolutionPrinter.print(vrp, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
