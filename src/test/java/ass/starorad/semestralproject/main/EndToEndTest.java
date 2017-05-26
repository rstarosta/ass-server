package ass.starorad.semestralproject.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import junit.framework.Assert;

import org.testng.annotations.Test;

public class EndToEndTest {

	@Test
	public void main() throws IOException, InterruptedException {
		testServer("hi\n", "HI");
		testServer("HI!\n", "hi!");
	}
	
	private void testServer(String input, String expectedOutput) throws IOException, InterruptedException {
		// run server
		Thread serverThread = new Thread(() -> {
			System.out.println("Starting server");
			try {
				Main.main(new String[]{});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		serverThread.start();
		
		// wait for server to start
		Thread.sleep(1000);
		
		Socket sock = new Socket("localhost", 8080);
		sock.getOutputStream().write(input.getBytes());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		String result = br.readLine();
		System.out.println("Result: " + result);
		Assert.assertEquals(expectedOutput, result);
	}
}
