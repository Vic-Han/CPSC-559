# CPSC-559 File Transfer Application Project

This project is a File Transfer Application developed for CPSC 559

## Dependencies

Make sure to have the following dependencies in your Java environment:

- `sqlite-jdbc-3.34.0.jar`
- `javafx-sdk-21.0.2`

Ensure both are located in `C:\Program Files\Java`

## Usage

To run the server, execute:

```bash
make server_run
```

To run the client, execute:

```bash
make client_run
```

## Troubleshooting Makefile Errors

If you receive some version of the following error(s): 

`make : The term 'make' is not recognized as the name of a cmdlet, function, script file, or operable program.` 

or 

`bash: make: command not found` 

on Windows, install MinGW and replace `make` with `mingw32-make`
