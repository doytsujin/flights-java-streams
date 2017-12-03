package airtraffic;

public interface PlaneReports {
   void reportTotalPlanesByManfacturer(Repository repository);
   void reportTotalPlanesByYear(Repository repository);
   void reportTotalPlanesByAircraftType(Repository repository);
   void reportTotalPlanesByEngineType(Repository repository);
   void reportPlanesWithMostCancellations(Repository repository);
   void reportMostFlightsByPlane(Repository repository);
   void reportMostFlightsByPlaneModel(Repository repository);
   void reportTotalFlightsByPlaneManufacturer(Repository repository);
   void reportTotalFlightsByPlaneAgeRange(Repository repository);
   void reportTotalFlightsByAircraftType(Repository repository);
   void reportTotalFlightsByEngineType(Repository repository);
}