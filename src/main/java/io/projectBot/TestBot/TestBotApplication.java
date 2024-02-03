package io.projectBot.TestBot;

import io.projectBot.TestBot.service.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@SpringBootApplication
public class TestBotApplication {



	public static void main(String[] args)
	{
		SpringApplication.run(TestBotApplication.class, args);

	}

}
