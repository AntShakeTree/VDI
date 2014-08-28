package test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vdi.config.PersistenceContext;

//@EnableWebMvc
@Configuration
@ComponentScan("com.vdi")
@Import(value={PersistenceContext.class})
public class TestConfig {

}
