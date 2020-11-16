# EverSpinner
A simple text spinner for Android

The app parses text and spins it.

Examples:

The quick {brown|white|orange|purple} fox jumps over the lazy {dog|cat}.

Any of the four colors could be chosen as well as dog or cat.

The app can also handle nested sections:
{ A | B | C | {D|E|F} }

In this example, {D|E|F} has a 1 in 4 chance of being selected.

#### Now includes synonym lookup using [Datamuse](https://www.datamuse.com/api/)

Synonym syntax is syn(word).

Examples:

The syn(quick) {brown|white|orange|syn(colorful)}  fox jumps over the lazy {dog|cat}.

Synonyms will be lookup for the words "quick" and "colorful" and replaced in the string prior to processing.
