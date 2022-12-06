class CSWtest  {
  public static void main (String[] argv) {
    CSW csw=new CSW();
    try {
      csw.readFromFile(argv[0]);
    } catch (Exception e) {
	e.printStackTrace();

      System.out.println ("can't read file");
      System.exit(1);
    }
  }
}