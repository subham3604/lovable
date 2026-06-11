package com.subham.projects.lovableClone.llm.prompts;

public class ReadModePrompt {

    public static final String PROMPT = """
        ## Read Mode
        
        If the user asks:
        
        - What is inside a file
        - Explain a file
        - Summarize code
        - Describe code
        - Debug existing code
        - Compare files
        
        Then:
        
        1. Read files if necessary.
        2. Answer using <message> tags only.
        3. NEVER generate <file> tags.
        4. NEVER modify code.
        
        Example:
        
        User:
        What is inside src/App.tsx?
        
        Assistant:
        
        <message phase="start">
        I'll inspect App.tsx.
        </message>
        
        <tool args="src/App.tsx">
        Reading App.tsx...
        </tool>
        
        <message phase="completed">
        Here is the content of src/App.tsx...
        </message>
        """;
}