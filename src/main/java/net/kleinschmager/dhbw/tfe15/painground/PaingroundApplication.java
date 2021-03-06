/**
 * copyright by Robert Kleinschmager
 */
package net.kleinschmager.dhbw.tfe15.painground;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import kr.pe.kwonnam.slf4jlambda.LambdaLogger;
import kr.pe.kwonnam.slf4jlambda.LambdaLoggerFactory;
import net.kleinschmager.dhbw.tfe15.painground.business.CsvImporter;
import net.kleinschmager.dhbw.tfe15.painground.persistence.model.MemberProfile;
import net.kleinschmager.dhbw.tfe15.painground.persistence.repository.MemberProfileRepository;

@SpringBootApplication
public class PaingroundApplication {
	
	private static final LambdaLogger log = LambdaLoggerFactory.getLogger(PaingroundApplication.class);
	
	@Autowired
	CsvImporter csvImporter;

	/**
	 * the main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaingroundApplication.class, args);
	}
	
	/**
	 * Using the CommandLineRunner feature of spring-boot
	 * 
	 * The annotation @Bean ensures, that my {@link CommandLineRunner} is in the spring context, 
	 * spring-boot ensures, that this runner is executed 
	 *  
	 */
	@Bean
	public CommandLineRunner loadData(MemberProfileRepository repository) {
		return args -> {
			
			saveSomeProfiles(repository);
			
			// STEP 2
			// save a couple of profiles
			
			// fetch all profiles
			log.info("MemberProfiles found with findAll():");
			log.info("-------------------------------");
			for (MemberProfile profile : repository.findAll()) {
				log.info(profile.toString());
			}
			log.info("");

			deleteAllExistingProfiles(repository);
			importProfiles(repository);
			fetchAndPrintAllProfiles(repository);
		};
	}

	private void deleteAllExistingProfiles(MemberProfileRepository repository) {
		repository.deleteAll();
		repository.flush();
	}

	private void fetchAndPrintAllProfiles(MemberProfileRepository repository) {
		log.info(() -> "MemberProfiles found with findAll():");
		log.info(() -> "-------------------------------");
		for (MemberProfile profile : repository.findAll()) {
			log.info(() -> "Profile: " + profile.toString());
		}
		log.info(() -> "");
	}


	private void saveSomeProfiles(MemberProfileRepository repository) {
		// save a couple of profiles
		repository.save(new MemberProfile("robkle", "Kleinschmager"));
		repository.save(new MemberProfile("mickni", "Knight"));
		repository.save(new MemberProfile("geolaf", "Laforge"));
		repository.save(new MemberProfile("Thomas", "Zetsche"));
		repository.save(new MemberProfile("homsim", "Simpsons"));
		repository.save(new MemberProfile("timo", "Bob"));

		repository.flush();
	}
	

	private void importProfiles(MemberProfileRepository repository) {
		
		URL inputFileUrl = PaingroundApplication.class.getClassLoader().getResource("db/initial_data.csv");
		
		File inputFile = new File(inputFileUrl.getFile());
		
		log.info(() -> "Reading file: " + inputFile.getAbsolutePath());
		
		List<MemberProfile> profiles = csvImporter.importFile(inputFile);
		
		for (MemberProfile memberProfile : profiles) {
			repository.save(memberProfile);
		}
		
		log.info(() -> "Imported " + profiles.size() + " profiles");
		
		repository.flush();
		
	}
}
