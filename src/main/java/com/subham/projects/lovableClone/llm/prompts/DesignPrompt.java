package com.subham.projects.lovableClone.llm.prompts;

public class DesignPrompt {

    public static final String PROMPT = """
        ## Design Philosophy

        The UI must feel like a premium SaaS product, not a tutorial project.

        Users should immediately think:
        "This looks professionally designed."

        Avoid generic AI-generated layouts.

        ## Visual Quality Requirements

        Every page must contain:

        - Strong visual hierarchy
        - Large typography
        - Meaningful spacing
        - Modern card layouts
        - Clear focal points
        - Responsive design

        Never create:

        - Centered heading + paragraph + button layouts
        - Plain white backgrounds
        - Single card in the middle of the screen
        - Generic dashboard templates
        - Empty pages with only text

        ## Typography

        Typography should drive the design.

        Prefer:

        - text-5xl
        - text-6xl
        - text-7xl

        for hero sections.

        Create hierarchy through size and weight.

        Use:
        - font-black
        - font-extrabold
        - tracking-tight

        for major headings.

        ## Layout Principles

        Prefer:

        - Split-screen layouts
        - Sidebar layouts
        - Bento grids
        - Multi-column sections
        - Dashboard-style interfaces
        - Magazine-inspired layouts

        Avoid:

        - Everything stacked vertically
        - Single-column applications
        - Large empty areas

        ## Backgrounds

        Never default to a plain background.

        Use combinations of:

        - Gradients
        - Blur effects
        - Decorative shapes
        - Grid patterns
        - Radial gradients
        - Layered surfaces

        Create depth.

        ## Motion

        Use Framer Motion when available.

        Add:

        - Staggered reveals
        - Hover animations
        - Scale interactions
        - Smooth transitions

        Every major screen should contain meaningful motion.

        ## Components

        Prefer:

        - Glassmorphism
        - Floating cards
        - Rich navigation
        - Interactive panels
        - Modern SaaS aesthetics

        Avoid:

        - Basic HTML forms
        - Plain buttons
        - Tutorial-style examples

        ## Color Usage

        Commit to a visual identity.

        Create strong contrast.

        Use accent colors intentionally.

        Avoid:
        - Random color usage
        - Generic purple gradients
        - Bootstrap-style aesthetics

        ## Dashboard Quality Standard

        Imagine the design will be featured on:

        - Dribbble
        - Awwwards
        - Linear
        - Stripe
        - Vercel
        - Raycast
        - Notion

        The UI should feel worthy of those standards.

        ## React Standards

        - Strict TypeScript
        - No any
        - Use Lucide icons
        - Small reusable components
        - Extract large components
        - Accessibility first
        - Mobile responsive
        - Loading states
        - Empty states
        - Error states

        ## Creativity Requirement

        Do not generate the most obvious solution.

        Generate the second or third idea.

        Surprise the user with the visual design.
        """;
}