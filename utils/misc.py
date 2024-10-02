from typing import Final

DEBUG_NONE: Final[int] = 1
DEBUG_IMPORTANT: Final[int] = 2
DEBUG_MORE: Final[int] = 3
DEBUG_IRRELEVANT: Final[int] = 4
DEBUG_VERBOSE: Final[int] = 5

currentDebug = DEBUG_MORE


def debug(message: str, priority: int = DEBUG_MORE):
    if priority <= currentDebug:
        print(message)


def set_debug_lvl(lvl: int):
    current_debug = lvl
