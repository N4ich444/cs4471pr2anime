package org.cs4471.helloworld;

import org.cs4471.helloworld.registry.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements ApplicationRunner {
	private int sleepTime = 5000;

	@Value("${service.registrar}")
	private String serviceRegistrar;

	@Value("${service.name}")
	private String serviceName;

	@Value("${service.url}")
	private String serviceURL;

	@Value("${service.desc}")
	private String serviceDesc;

	@Autowired
	private RegistryService registryService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println(String.format("%s : Starting service with URL %s. Description: %s", serviceName, serviceURL, serviceDesc));

		// Connect to service controller
		registryService.Set(serviceRegistrar, serviceName,serviceURL, serviceDesc);
		System.out.println(String.format("%s : Connecting to %s", serviceName, serviceRegistrar));

		// Broadcast to service registry
		int retries = 6;
		try {
			while (true) {
				Response status = registryService.Register();

				if (status.getCode() == 200) {
					System.out.println(String.format("%s : Registered service!", serviceName));
					break;
				}
				else {
					retries--;
					if (retries <= 0) {
						System.out.println(String.format("%s : Failed to register, exiting...", serviceName));
						System.exit(1);
						break;
					}

					System.out.println(
							String.format("%s : Failed to register... retrying (%d attempts left)", serviceName, retries)
					);
					Thread.sleep(sleepTime);
				}
			}
		}
		catch (Exception e) {}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
