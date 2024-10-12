package org.cs4471.helloworld;

import org.cs4471.helloworld.registry.RegistryService;
import org.cs4471.helloworld.registry.RegistryStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {
	public String serviceName = "HelloWorld";

	@Autowired
	private RegistryService registryService;

	public void start(String url) {
		// Connect to service controller
		registryService.Set(serviceName, url);
		System.out.println(String.format("%s : Connecting to %s", serviceName, url));

		// Broadcast to service registry
		int retries = 6;
		boolean success = false;

		try {
			while (!success) {
				RegistryStatus.REGISTRY_STATUS status = registryService.Register();

				switch (status) {
					case SUCCESS:
						System.out.println(String.format("%s : Registered service!", serviceName));
						success = true;
						break;
					case EXISTS:
						System.out.println(String.format("%s : This URL has already been registered to the server!", serviceName));
						System.exit(1);
						break;
					case FAILURE:
						retries--;
						if (retries <= 0) {
							System.out.println(String.format("%s : Failed to register, exiting...", serviceName));
							System.exit(1);
						}
						System.out.println(
								String.format("%s : Failed to register... retrying (%d attempts left)", serviceName, retries)
						);
						Thread.sleep(5000);
						break;
					case BUSY:
						retries--;
						if (retries <= 0) {
							System.out.println(String.format("%s : The register is still busy after 5 tries. Exiting...", serviceName));
							System.exit(1);
						}
						System.out.println(
								String.format("%s : The registry is busy... retrying (%d attempts left)", serviceName, retries)
						);
						Thread.sleep(5000);
						break;
				}
			}
		}
		catch (Exception e) {}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		start(args.getSourceArgs()[0]);
	}

	public static void main(String[] args) {
		// Terminate if no arguments supplied
		if (args.length == 0) {
			System.out.println("No server URL argument provided! Terminating...");
			System.exit(1);
		}

		SpringApplication.run(Application.class, args);
	}
}
