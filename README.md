# Overview

The `ds-client` program is a Job scheduler using the `ds-sim` protocol. 
The `ds-server` program and more information can be found at [distsys-MQ/ds-sim](https://github.com/distsys-MQ/ds-sim).
The protocol documentation can be found [here](https://github.com/distsys-MQ/ds-sim/blob/master/docs/ds-sim_user-guide.pdf).

## Testing

The project requires at least Java 11, older versions may also work but are unsupported.

On Linux you can run the following command, which will compile the project and run the tests.
```bash
./tests/test.sh
```

On Windows you can run the tests with the following command using WSL.
```powershell
wsl bash ./tests/test.sh
```

### Comparison Testing
You can use the `test_results` binary to gather performance data on the current
algorithm and compare it to the 3 baseline algorithms and ATL. 

Linux
```bash
./tests/run_test_results.sh -o co
```

Windows
```powershell
wsl bash ./tests/run_test_results.sh -o co
```
