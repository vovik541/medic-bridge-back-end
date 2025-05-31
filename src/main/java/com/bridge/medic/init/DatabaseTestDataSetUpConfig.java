package com.bridge.medic.init;

import com.bridge.medic.auth.service.AuthenticationService;
import com.bridge.medic.config.security.authorization.RoleEnum;
import com.bridge.medic.config.security.authorization.model.Role;
import com.bridge.medic.config.security.authorization.repozitory.RoleRepository;
import com.bridge.medic.mail.EmailDetails;
import com.bridge.medic.mail.EmailService;
import com.bridge.medic.specialist.model.DoctorType;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import com.bridge.medic.specialist.repository.DoctorTypeRepository;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.specialist.repository.SpecialistDoctorTypeRepository;
import com.bridge.medic.user.location.model.City;
import com.bridge.medic.user.location.model.Country;
import com.bridge.medic.user.location.model.Region;
import com.bridge.medic.user.location.repository.CityRepository;
import com.bridge.medic.user.location.repository.CountryRepository;
import com.bridge.medic.user.location.repository.RegionRepository;
import com.bridge.medic.user.model.Language;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.LanguageRepository;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("all")
@Configuration
@RequiredArgsConstructor
public class DatabaseTestDataSetUpConfig {

    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorTypeRepository doctorTypeRepository;
    private final SpecialistDataRepository specialistDataRepository;
    private final SpecialistDoctorTypeRepository specialistDoctorTypeRepository;
    private final EmailService emailService;

    private final List<String> doctorTypes = new LinkedList<>() {{
        this.add("Дієтолог");
        this.add("Дерматолог");
        this.add("Невролог");
        this.add("Педіатр");
        this.add("Ендокринолог");
    }};

    private final void initRoles() {
        if (roleRepository.findByName(RoleEnum.USER.name()).isPresent()) {
            return;
        }

        List<Role> roles = new LinkedList<>() {{
            this.add(new Role(RoleEnum.USER.name()));
            this.add(new Role(RoleEnum.ADMIN.name()));
            this.add(new Role(RoleEnum.SPECIALIST.name()));
            this.add(new Role(RoleEnum.SUPPORT.name()));
        }};

        roleRepository.saveAll(roles);
    }


    private final void initLocations() {
        if (!countryRepository.findAll().isEmpty()) {
            return;
        }

        City vinnytsia = City.builder().name("Вінниця").build();
        City chernivtsy = City.builder().name("Чернівці").build();
        City kyiv = City.builder().name("Київ").build();
        City pereiaslav = City.builder().name("Переяслав").build();
        City lviv = City.builder().name("Lviv").build();
        City warsaw = City.builder().name("Варшава").build();

        Region vinnytsiaRegion = Region.builder()
                .name("Вінницька")
                .cities(List.of(vinnytsia, chernivtsy))
                .build();
        Region kyivRegion = Region.builder()
                .name("Київська")
                .cities(List.of(kyiv, pereiaslav))
                .build();
        Region lvivRegion = Region.builder()
                .name("Львывська")
                .cities(List.of(lviv))
                .build();
        Region warsawRegion = Region.builder()
                .name("Варшавська")
                .cities(List.of(warsaw))
                .build();

        vinnytsia.setRegion(vinnytsiaRegion);
        chernivtsy.setRegion(vinnytsiaRegion);
        kyiv.setRegion(kyivRegion);
        pereiaslav.setRegion(kyivRegion);
        lviv.setRegion(lvivRegion);
        warsaw.setRegion(warsawRegion);

        Country ukraine = Country.builder()
                .isoCode("UA")
                .name("Україна")
                .regions(List.of(vinnytsiaRegion, kyivRegion, lvivRegion))
                .build();
        Country poland = Country.builder()
                .isoCode("PL")
                .name("Польща")
                .regions(List.of(warsawRegion))
                .build();

        vinnytsiaRegion.setCountry(ukraine);
        kyivRegion.setCountry(ukraine);
        lvivRegion.setCountry(ukraine);
        warsawRegion.setCountry(poland);

        countryRepository.saveAll(List.of(ukraine, poland));
    }

    private final void initLanguages() {
        if (!languageRepository.findAll().isEmpty()) {
            return;
        }

        languageRepository.saveAll(List.of(
                Language.builder().name("Українська").build(),
                Language.builder().name("Польська").build(),
                Language.builder().name("Англійська").build()
        ));
    }

    private final void initUsers() {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        User admin = User.builder()
                .firstName("Volodymyr")
                .lastName("Khymych")
                .email("conanzhill@gmail.com")
                .login("vovik541")
                .isLocked(false)
                .password(passwordEncoder.encode("password"))//NOSONAR
                .languages(languageRepository.findAll())
                .registrationDate(LocalDateTime.now())
                .roles(roleRepository.findAll())
                .city(cityRepository.findByName("Вінниця").get())
                .image_url("https://cdn.pixabay.com/photo/2024/01/29/20/40/cat-8540772_640.jpg")
                .build();

        userRepository.save(admin);

        List<User> users = new LinkedList<>();
        User specialist;
        Random random = new Random();
        List<City> cities = cityRepository.findAll();
        SpecialistData specialistData;
        SpecialistDoctorType specialistDoctorType;
        List<DoctorType> doctorTypes = doctorTypeRepository.findAll();

        for (int i = 1; i < 40; i++) {
            int count = random.nextBoolean() ? 1 : 2;
            List<DoctorType> copy = new ArrayList<>(doctorTypes);
            Collections.shuffle(copy);
            List<DoctorType> selected = copy.subList(0, count);

            Collections.shuffle(cities);
            specialist = createBasicUser("doctor", i, RoleEnum.SPECIALIST, cities.getFirst());
            specialistData = SpecialistData.builder()
                    .user(specialist)
                    .build();

            for (DoctorType doctorType : selected) {
                specialistDoctorType = SpecialistDoctorType.builder()
                        .specialistData(specialistData)
                        .approved(true)
                        .approvedBy(userRepository.findByLogin("vovik541").get())
                        .doctorType(doctorType)
                        .build();
                specialistData.addSpecialistDoctorType(specialistDoctorType);
                specialist.setSpecialistData(specialistData);
            }
            users.add(specialist);
        }

        for (int i = 0; i < 10; i++) {
            Collections.shuffle(cities);
            users.add(createBasicUser("user", i, RoleEnum.USER, cities.getFirst()));
        }
        userRepository.saveAll(users);

    }

    private User createBasicUser(String tag, int num, RoleEnum roleEnum, City city) {
        return User.builder()
                .firstName("Fn" + tag + num)
                .lastName("Ln" + tag + num)
                .email(tag + num + "@gmail.com")
                .login("login" + tag + num)
                .isLocked(false)
                .password(passwordEncoder.encode("password"))//NOSONAR
                .languages(List.of(languageRepository.findByName("Українська").get(),
                        languageRepository.findByName("Англійська").get()))
                .registrationDate(LocalDateTime.now())
                .roles(List.of(roleRepository.findByName(roleEnum.name()).get()))
                .city(city)
                .image_url("https://cdn.pixabay.com/photo/2024/01/29/20/40/cat-8540772_640.jpg")
                .build();
    }

    private final void initDoctorTypes() {
        if (!doctorTypeRepository.findAll().isEmpty()) {
            return;
        }

        for (String type : doctorTypes) {
            doctorTypeRepository.save(DoctorType.builder()
                    .name(type)
                    .build());
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            initRoles();
            initLocations();
            initLanguages();
            initDoctorTypes();
            initUsers();
            System.out.println("INITIALISED DB instances");
//            testMailSender();
        };
    }
    private void testMailSender(){
        emailService.sendSimpleMail(EmailDetails.builder()
                .recipient("conanzhill@gmail.com")
                .msgBody("Testing message")
                .subject("Medic-Bridge test")
                .build());
    }
}