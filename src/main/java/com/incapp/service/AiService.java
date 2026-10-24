package com.incapp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

	private final ChatClient chatClient;

	public AiService(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}
	public String askAi(String actAs,String prompt) {
		String r = chatClient.prompt()
				.system(actAs)
				.user(prompt)
				.call()
				.content();
		return r;
	}
}
