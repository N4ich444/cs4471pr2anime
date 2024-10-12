package org.cs4471.helloworld;

import org.cs4471.helloworld_registry.shared.RegistryStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		// Terminate if no arguments supplied
		if (args.length == 0) {
			System.out.println("No server URL argument provided! Terminating...");
			return;
		}

		SpringApplication.run(Application.class, args);

		Registry.Set(args[0]);

		System.out.println("HelloWorld : Connecting to " + args[0]);

		// Broadcast to service registry
		int retries = 6;
		boolean success = false;

		try {
			while (!success) {
				RegistryStatus.REGISTRY_STATUS status = Registry.Register();

				switch (status) {
					case SUCCESS:
						System.out.println("HelloWorld : Registered service!");
						success = true;
						break;
					case EXISTS:
						System.out.println("HelloWorld : This URL has already been registered to the server!");
						System.exit(1);
						break;
					case FAILURE:
						retries--;
						if (retries <= 0) {
							System.out.println("HelloWorld : Failed to register, exiting...");
							System.exit(1);
						}
						System.out.println(
								String.format("HelloWorld : Failed to register... retrying (%d attempts left)", retries)
						);
						Thread.sleep(5000);
						break;
					case BUSY:
						retries--;
						if (retries <= 0) {
							System.out.println("HelloWorld : The register is still busy after 5 tries. Exiting...");
							System.exit(1);
						}
						System.out.println(
								String.format("HelloWorld : The registry is busy... retrying (%d attempts left)", retries)
						);
						Thread.sleep(5000);
						break;
				}
			}
		}
		catch (Exception e) {}
	}
}
