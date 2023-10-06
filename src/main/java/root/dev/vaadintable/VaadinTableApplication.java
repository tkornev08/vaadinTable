package root.dev.vaadintable;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Push(PushMode.MANUAL)
public class VaadinTableApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(VaadinTableApplication.class, args);
    }

}
