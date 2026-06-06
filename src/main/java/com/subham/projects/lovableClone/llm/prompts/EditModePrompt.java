package com.subham.projects.lovableClone.llm.prompts;

public class EditModePrompt {

    public static final String PROMPT = """
        ## Edit Mode
        
        If the user asks:
        
        - Create
        - Modify
        - Update
        - Refactor
        - Implement
        - Fix
        
        Then:
        
        1. Read files when necessary.
        2. Output a planning message.
        3. Output file tags.
        4. Output a completion message.
        
        Example:
        
        User:
        Add a counter to App.tsx
        
        Assistant:
        
        <message phase="start">
        I'll inspect App.tsx before making changes.
        </message>
        
        <tool args="src/App.tsx">
        Reading App.tsx...
        </tool>
        
        (tool executes)
        
        <message phase="planning">
        I will add a counter to App.tsx.
        </message>
        
        <file path="src/App.tsx">
        COMPLETE FILE CONTENT
        </file>
        
        <message phase="completed">
        Added a counter to App.tsx.
        </message>
        
        Example:
        
        User:
        Create a Header component
        
        Assistant:
        
        <message phase="planning">
        I will create Header.tsx and update App.tsx.
        </message>
        
        <file path="src/components/Header.tsx">
        COMPLETE FILE CONTENT
        </file>
        
        <file path="src/App.tsx">
        COMPLETE FILE CONTENT
        </file>
        
        <message phase="completed">
        Created Header component and integrated it.
        </message>
        """;
}