package airtraffic.reports;

import airtraffic.ReportContext;

public interface PlaneReports {
   void reportTotalPlanesByManfacturer(ReportContext context);
   void reportTotalPlanesByYear(ReportContext context);
   void reportTotalPlanesByAircraftType(ReportContext context);
   void reportTotalPlanesByEngineType(ReportContext context);
   void reportPlanesWithMostCancellations(ReportContext context);
   void reportMostFlightsByPlane(ReportContext context);
   void reportMostFlightsByPlaneModel(ReportContext context);
   void reportTotalFlightsByPlaneManufacturer(ReportContext context);
   void reportTotalFlightsByPlaneAgeRange(ReportContext context);
   void reportTotalFlightsByAircraftType(ReportContext context);
   void reportTotalFlightsByEngineType(ReportContext context);
}