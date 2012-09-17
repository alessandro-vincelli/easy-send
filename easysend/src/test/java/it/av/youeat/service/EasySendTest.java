package it.av.youeat.service;

import it.av.es.EasySendException;
import it.av.es.model.Language;
import it.av.es.model.UserProfile;
import it.av.es.service.LanguageService;
import it.av.es.service.UserProfileService;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class EasySendTest {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private LanguageService languageService;
    private UserProfile profile;
    private Language language;

    public void setUp() throws EasySendException {
        profile = new UserProfile();
        profile.setName("testProfile");
        profile = userProfileService.save(profile);
        if (userProfileService.getByName("USER") == null) {
            UserProfile userProfile = new UserProfile("USER");
            userProfile = userProfileService.save(userProfile);
        }
        if (userProfileService.getByName("ADMIN") == null) {
            UserProfile adminProfile = new UserProfile("ADMIN");
            adminProfile = userProfileService.save(adminProfile);
        }
        if(languageService.getAll().size() == 0){
            languageService.save(new Language("it", "italy"));
            languageService.save(new Language("en", "usa"));
        }
        language = languageService.getAll().get(0);
    }
    
    public void tearDown() throws EasySendException {
        Collection<UserProfile> all2 = userProfileService.getAll();
        for (UserProfile userProfile : all2) {
            userProfileService.remove(userProfile);
        }
        List<Language> all = languageService.getAll();
        for (Language language : all) {
            languageService.remove(language);
        }
        
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Language getLanguage() {
        return language;
    }
    
}