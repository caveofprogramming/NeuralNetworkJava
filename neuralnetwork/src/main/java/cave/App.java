package cave;

import java.io.File;

public class App {

	public static void main(String[] args) {
		
		if(args.length == 0 && new File(args[0]).isDirectory()) {
			System.out.println("usage: [app] <MNIST DATA DIRECTORY>");
			return;
		}
		
		System.out.println("Hello");
	

	}

}
