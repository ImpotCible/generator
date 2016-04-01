

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestFormaterDate {

  public static void main(String[] args) {
    SimpleDateFormat formater = null;

    Date aujourdhui = new Date();

    formater = new SimpleDateFormat("dd-MM-yy");
    System.out.println(formater.format(aujourdhui));
   
  }
}

