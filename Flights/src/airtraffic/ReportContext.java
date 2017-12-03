package airtraffic;

import org.beryx.textio.TextTerminal;

public final class ReportContext {
   private String state;
   private Airport airport;
   private Airport origin;
   private Airport destination;
   private Carrier carrier;
   private GeoLocation location;
   private Repository repository;
   private TextTerminal<?> terminal;
   private int distance;
   private int limit = Integer.MAX_VALUE;
   private int year;

   public String getState() {
      return state;
   }

   public ReportContext setState(String state) {
      this.state = state;
      return this;
   }

   public Airport getAirport() {
      return airport;
   }

   public ReportContext setAirport(Airport airport) {
      this.airport = airport;
      return this;
   }

   public Airport getOrigin() {
      return origin;
   }

   public ReportContext setOrigin(Airport origin) {
      this.origin = origin;
      return this;
   }

   public Airport getDestination() {
      return destination;
   }

   public ReportContext setDestination(Airport destination ) {
      this.destination = destination;
      return this;
   }

   public Carrier getCarrier() {
      return carrier;
   }

   public ReportContext setCarrier(Carrier carrier) {
      this.carrier = carrier;
      return this;
   }

   public GeoLocation getLocation() {
      return location;
   }

   public ReportContext setLocation(GeoLocation location) {
      this.location = location;
      return this;
   }

   public Repository getRepository() {
      return repository;
   }

   public ReportContext setRepository(Repository repository) {
      this.repository = repository;
      return this;
   }

   public TextTerminal<?> getTerminal() {
      return terminal;
   }

   public ReportContext setTerminal(TextTerminal<?> terminal) {
      this.terminal = terminal;
      return this;
   }

   public int getDistance() {
      return distance;
   }

   public ReportContext setDistance(int distance) {
      this.distance = distance;
      return this;
   }

   public int getLimit() {
      return limit;
   }

   public ReportContext setLimit(int limit) {
      this.limit = limit;
      return this;
   }

   public int getYear() {
      return year;
   }

   public ReportContext setYear(int year) {
      this.year = year;
      return this;
   }
}