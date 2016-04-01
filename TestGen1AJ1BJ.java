
public class TestGen1AJ1BJ {

  public static void main(String[] args) {
	  
	//cas couple
    String revenu1, revenu2, revenufinal;
    int higher = 5000;
    int lower = 800;
    int random = (int)(Math.random() * (higher-lower)) + lower;
    revenu1 = "1AJ" + random;
    random = (int)(Math.random() * (higher-lower)) + lower;
    revenu2 = "1BJ" + random; 
    revenufinal = revenu1 + "#" + revenu2;
    
    System.out.println(revenufinal);
  }
}

