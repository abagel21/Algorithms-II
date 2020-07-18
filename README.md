# Princeton Algorithms I on Coursera
This repository contains all the projects in the course Algorithms Part I.


## WordNet
WordNet was practice with graph concepts and fundamental searching algorithms--BFS and DFS.

I implemented the WordNet data type that took in synsets and hypernyms (collections of related words and connections between related synsets) and put it in a directed graph while also storing all words and their occurences and all synsets.

Then I implemented the Shorted Ancestral Path data type that found the shortest connection between two words using a special modified BFS that started from multiple locations and returned the path length and ancestor when the searches met.

This allowed me to implement the "Outcast" data type that could find the least related term in a set of terms using SAP.


## Seam Carving
Seam carving is the process of resizing an image by removing or adding the lowest "weight"/"priority" seam in the image, calculated with a gradient equation. This project practiced shorted path algorithms and weighted graphs.

I implemented it with an implied DAG (never actually creating the graph to save space and time) that found the shortest weight path from each top pixel to any bottom pixel (or each right pixel to any left pixel) using the edge-weighted directed acyclic graph algorithm for shortest paths and picked the smallest weighted path to remove by creating a new smaller photo and copying all colors except the seam over.


## Baseball Elimination
This project practiced max-flow and min-cut with flow networks by implementing a way of calculating which teams are mathematically eliminated in a baseball division. My ADT created a directed graph with two extra nodes s and t to calculate max flow, with each team connected to t by the maximum number of games they can win without beating the parameter team and s connected to each matchup by the number of remaining games to be played and each matchup connected to the two teams involved. After max-flow is calculated, if it isn't equal to the total number of games left to be played, then there is no way that all games can be played and the parameter team has the most wins. Any team in the min cut is the subset of teams that have the set of games that can't be played without beating the parameter team.


## Boggle
This project, directed at tries and ternary search tries, set the goal of creating the fastest possible abstract data type for solving Boggle boards. I implemented a specific data type to store the dictionary designed for rapid insert, get, and prefixing operations (the latter meaning checking if a string is the prefix of any word in the dictionary to rule out certain recursive paths). It was a 26-squared way trie at the top holding every two letter combination in the alphabet, with each node in the 26-squared array implicitly corresponding to that two letter combination and acting as the root node of a ternary search trie for the remaining characters past two.

The BoggleSolver class used this data type to store the dictionary, and did a depth-first search on the implicit graph of the Boggle Board to locate all possible combinations of adjacent letters for every square, stopping when hitting a marked square or the current string wasn't a prefix of any word in the dictionary, and adding the string when it is a valid word.

## Burrows-Wheeler
The final project was directed at compression, specifically Burrows-Wheeler compression. I was given a description of the process and left to implement three things-Move To Front encoding, the CircularSuffixArray data type, and the actual Burrows-Wheeler process. 

Move To Front encoding took a bitstream input and used a radix array, and every time a character came up return its index and move that character to the front of the radix array. Decoding was the reverse of the process-- returning the character and moving it to the front as it received indices.

CircularSuffixArray, to save memory and time, didn't every create an actual suffix array, but used a specific Comparator to compare two implicit suffixes with the location of their starting character in the give string. Using this it sorted the implicit suffixes and stored the original indices in the circular suffix array of the items in the sorted circular suffix array.

Burrows-Wheeler implemented the transform by printing out the sorted circular suffix array location of the first suffix, and the last characters in each circular suffix in the sorted array. The reverse transform involved calculating a next array to reestablish the order of the suffixes, taking advantage of the fact that the first letters of the suffixes were the sorted last letters of the suffixes, so suffix i could be located by finding what the sorted position of the last letter of suffix i + 1 with key-indexed counting (allowing linear time). Then by following the next array, each letter in the original key was printed out in order.