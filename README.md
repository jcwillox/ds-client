# Overview

The `ds-client` program is a Job scheduler using the `ds-sim` protocol. 
The `ds-server` program and more information can be found at [distsys-MQ/ds-sim](https://github.com/distsys-MQ/ds-sim).
The protocol documentation can be found [here](https://github.com/distsys-MQ/ds-sim/blob/master/docs/ds-sim_user-guide.pdf).

## Testing

The project requires at least Java 11, older versions may also work but are unsupported.

On Linux you can run the following command, which will compile the project and run the tests.
```bash
./tests/run1.sh
```

On Windows you can run the tests with the following command using WSL.
```powershell
wsl bash ./tests/run1.sh
```
