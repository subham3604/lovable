package com.subham.projects.lovableClone.llm;

import com.subham.projects.lovableClone.llm.prompts.BasePrompt;
import com.subham.projects.lovableClone.llm.prompts.DesignPrompt;
import com.subham.projects.lovableClone.llm.prompts.EditModePrompt;
import com.subham.projects.lovableClone.llm.prompts.ReadModePrompt;

import java.time.LocalDateTime;

public class PromptUtil {

    public static final String CODE_GENERATION_SYSTEM_PROMPT =
            """
            Current Time:
            """ + LocalDateTime.now() + "\n\n"
                    + BasePrompt.PROMPT + "\n\n"
                    + ReadModePrompt.PROMPT + "\n\n"
                    + EditModePrompt.PROMPT + "\n\n"
                    + DesignPrompt.PROMPT;
}