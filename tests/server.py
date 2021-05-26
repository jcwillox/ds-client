#!/usr/bin/env python3
import argparse
import logging
from difflib import unified_diff
from os import chdir
from os.path import dirname, realpath, abspath, relpath
from subprocess import DEVNULL

from pwnlib.tubes.process import process
from rich import traceback
from rich.console import Console
from rich.logging import RichHandler

console = Console(force_terminal=True)
traceback.install(console=console)

# noinspection PyArgumentList
logging.basicConfig(
    level="NOTSET",
    format="%(message)s",
    datefmt="[%X]",
    handlers=[RichHandler(console=console, rich_tracebacks=True, markup=True)],
)
logging.getLogger("pwnlib").setLevel("ERROR")
log = logging.getLogger("tests")

DEFAULT_CONFIG = "./S1testConfigs/ds-S1-config01--wk6.xml"
DEFAULT_ALGORITHM = "bf"
BASE_DIR = dirname(realpath(__file__))

parser = argparse.ArgumentParser()
parser.add_argument("-c", "--config", nargs="*")
parser.add_argument("-v", "--verbose", action="store_true")
parser.add_argument("-q", "--quiet", action="store_true")
parser.add_argument("--once", action="store_true")
parser.add_argument("--client", help="commandline args to automatically run the client")
parser.add_argument(
    "-o", "--output", action="store_true", help="write expected and result to files"
)
parser.add_argument(
    "-a",
    "--algorithm",
    choices=["f", "ff", "bf", "wf", "lf"],
    default=DEFAULT_ALGORITHM,
)


def diff(before, after, verbose):
    lines = unified_diff(
        before.splitlines(keepends=True), after.splitlines(keepends=True)
    )

    max_lines = 100 if verbose else 25

    for idx, line in enumerate(lines):
        if idx > max_lines:
            console.print(f"[... truncating as longer than {max_lines} lines ...]")
            break
        if line.startswith("-"):
            console.print("[red]" + line, end="")
        elif line.startswith("+"):
            console.print("[green]" + line, end="")
        else:
            console.print(line, end="")


def main(
    config: str,
    algorithm: str,
    write_output: bool,
    once: bool,
    client_command: str,
    verbose: bool,
    quiet: bool,
):
    server_args = ["./ds-server", "-c", config, "-v", "brief", "-n"]
    client_args = ["./ds-client", "-n"]
    if algorithm != "f":
        client_args.extend(["-a", algorithm])

    log.info("[red]CONFIG[/]: '%s'", relpath(config))
    log.debug("Retrieving expected output from ds-client")

    server = process(server_args)
    expected = server.recvuntil("Waiting for connection").decode()
    client = process(client_args, stdout=DEVNULL)
    expected += server.recvall().decode()
    client.wait_for_close()

    log.debug("Successfully extracted the expected output")

    if write_output:
        with open("ds-server-expected.log", "w") as file:
            file.write(expected)

    with console.status("[bold green]Waiting for client..."):
        while True:
            server = process(server_args)

            log.info(f"Started './ds-server' (pid: {server.proc.pid})")

            output = ""

            if client_command:
                output += server.recvuntil("Waiting for connection").decode()
                output += server.recvline().decode()
                if verbose:
                    console.print(output, end="")
                client = process(client_command.split(), stdout=DEVNULL)

            try:
                line = server.recvline().decode()
                while line:
                    output += line
                    if verbose:
                        console.print(line, end="")
                    line = server.recvline().decode()
            except EOFError:
                pass

            log.info("Finished reading output from server")

            if expected != output:
                if not quiet:
                    diff(expected, output, verbose)
                if write_output:
                    with open("ds-server-output.log", "w") as file:
                        file.write(output)
                console.rule("[bold red]ERROR", style="bright_black")
            else:
                console.rule("[bold green]SUCCESS", style="bright_black")

            if client_command:
                client.wait_for_close(4)

            if once or client_command:
                break


if __name__ == "__main__":
    args = parser.parse_args()
    if args.quiet:
        log.setLevel("INFO")

    if args.config:
        configs = [abspath(config) for config in args.config]
    else:
        configs = [DEFAULT_CONFIG]

    chdir(BASE_DIR)

    log.info("[red]ALGORITHM[/]: '%s'", args.algorithm)
    console.rule(style="bright_black")

    try:
        for config in configs:
            main(
                config,
                args.algorithm,
                args.output,
                args.once,
                args.client,
                args.verbose,
                args.quiet,
            )
    except KeyboardInterrupt:
        pass
