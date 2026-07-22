# Contributing

Thanks for your interest in contributing to Epic Fight X MineColonies Compat!

## Getting Started

1. Fork the repository.
2. Clone your fork.
3. Import the project into your IDE.
4. Make your changes.
5. Test the mod with the required Minecraft, NeoForge, Epic Fight, and MineColonies versions.
6. Commit your changes.
7. Push your branch to your fork.
8. Open a Pull Request.

## Pull Requests

Please explain:
- What you changed.
- Why the change is needed.
- How you tested it.

For compatibility fixes, please include the relevant:
- Minecraft version
- NeoForge version
- Epic Fight version
- MineColonies version

## Code Guidelines

- Keep changes focused on the issue being fixed.
- Avoid unnecessary changes to unrelated code.
- Follow the existing code style.
- Do not include copyrighted assets from other mods.
- Do not add replacement MineColonies models or textures unless specifically required.

## Compatibility Contributions

When adding support for new MineColonies models or entity variants, prefer the existing dynamic mesh conversion system where possible.

Please avoid hardcoding individual mesh assets when the model can be converted dynamically.

## Testing

Before submitting a Pull Request, verify that:

- The game launches successfully.
- MineColonies citizens render correctly.
- Epic Fight combat works as expected.
- MineColonies AI remains functional.
- No unnecessary mesh regeneration occurs.
- Existing supported entities continue to work.

## Issues

Bug reports and feature requests are welcome through GitHub Issues.

Please provide logs and reproduction steps when reporting bugs.
