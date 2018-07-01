package airtraffic.reports;

import java.sql.ResultSet;
import airtraffic.ReportContext;

public interface PlaneReports {
   ResultSet reportTotalPlanesByManfacturer(ReportContext context);
   ResultSet reportTotalPlanesByYear(ReportContext context);
   ResultSet reportTotalPlanesByAircraftType(ReportContext context);
   ResultSet reportTotalPlanesByEngineType(ReportContext context);
   ResultSet reportPlanesWithMostCancellations(ReportContext context);
   ResultSet reportMostFlightsByPlane(ReportContext context);
   ResultSet reportMostFlightsByPlaneModel(ReportContext context);
   ResultSet reportTotalFlightsByPlaneManufacturer(ReportContext context);
   ResultSet reportTotalFlightsByPlaneAgeRange(ReportContext context);
   ResultSet reportTotalFlightsByAircraftType(ReportContext context);
   ResultSet reportTotalFlightsByEngineType(ReportContext context);
}