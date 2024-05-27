
# Bayesian Network Inference with Variable Elimination

This project implements a Bayesian Network inference engine using Variable Elimination and the Bayes Ball algorithm to determine conditional independence. The program reads a Bayesian Network from an XML file, processes queries from an input file, and outputs the results.

## Features

- **Bayesian Network Parsing**: Parse Bayesian Network structures and Conditional Probability Tables (CPTs) from XML files.
- **Bayes Ball Algorithm**: Determine conditional independence between nodes given evidence.
- **Variable Elimination Algorithm**: Perform exact inference to compute the probability distribution of query variables given evidence.

## File Structure

- `Ex1.java`: Main class to execute the program.
- `BayesianNetwork.java`: Represents the Bayesian Network.
- `Node.java`: Represents a node in the Bayesian Network.
- `Factor.java`: Represents a factor used in Variable Elimination.
- `VariableElimination.java`: Implements the Variable Elimination algorithm.
- `BayesBall.java`: Implements the Bayes Ball algorithm to determine conditional independence.
- `FactorOperationResult.java`: Stores the result of a factor operation.
- `Query.java`: Parses and stores query information.

## Requirements

- Java 8 or higher

## Usage

1. **Prepare Input Files**:
   - `input.txt`: This file should contain the path to the Bayesian Network XML file on the first line, followed by queries on subsequent lines.
   - Example `input.txt`:
     ```
     path/to/network.xml
     P(B=T|J=T,M=T) A-E
     A-B|E1=e1,E2=e2
     ```

2. **Compile the Code**:
   ```sh
   javac Ex1.java
   ```

3. **Run the Program**:
   ```sh
   java Ex1
   ```
   The program will read from `input.txt` and write the results to `output.txt`.

## Query Format

- **Variable Elimination Query**:
  ```
  P(QueryVariable=Value|EvidenceVariables) HiddenVariables
  ```
  - Example: `P(B=T|J=T,M=T) A-E`

- **Bayes Ball Query**:
  ```
  NodeA-NodeB|EvidenceVariables
  ```
  - Example: `A-B|E1=e1,E2=e2`

## Output Format

The output file `output.txt` will contain the results of each query in the format:
```
<probability>,<number of additions>,<number of multiplications>
```
For Bayes Ball queries, the output will be either "yes" or "no".

- Example Output:
  ```
  0.28417,7,16
  yes
  ```

## Classes and Methods

### Ex1.java

- **main(String[] args)**: Reads input, processes queries, and writes output.
- **processBayesBallQuery(BayesianNetwork network, Query query)**: Processes a Bayes Ball query to determine conditional independence.
- **processVariableEliminationQuery(BayesianNetwork network, Query query)**: Processes a variable elimination query to compute the probability distribution.

### BayesianNetwork.java

- **BayesianNetwork()**: Constructs an empty Bayesian network.
- **BayesianNetwork(BayesianNetwork network)**: Constructs a copy of the given Bayesian network.
- **addNode(Node node)**: Adds a node to the Bayesian network.
- **getNode(String name)**: Retrieves a node by its name.
- **getNodes()**: Returns a collection of all nodes in the network.
- **removeNode(String name)**: Removes a node by its name.
- **toString()**: Returns a string representation of the Bayesian network.
- **parseXML(String filename)**: Parses a Bayesian network from an XML file.
- **removeIrrelevantNodes(Query query)**: Removes irrelevant nodes from the network based on the query.

### Node.java

- **Node(String name)**: Constructs a new Node with the given name.
- **Node(Node node)**: Constructs a copy of the given Node.
- **getName()**: Returns the name of the node.
- **addOutcome(String outcome)**: Adds an outcome to the node.
- **getOutcomes()**: Returns the list of outcomes for the node.
- **addParent(Node parent)**: Adds a parent node to the node.
- **getParents()**: Returns the list of parent nodes.
- **addChild(Node child)**: Adds a child node to the node.
- **getChildren()**: Returns the list of child nodes.
- **toString()**: Returns a string representation of the node.
- **generateCPT(List<Double> probabilities)**: Generates the Conditional Probability Table (CPT) for the node using the given probabilities.
- **getCPT()**: Returns the Conditional Probability Table (CPT) for the node.
- **toFactor()**: Converts the node to a factor representation.

### Factor.java

- **Factor(List<String> columnNames, Map<List<String>, Double> table)**: Constructs a new Factor with the given column names and table.
- **getColumnNames()**: Returns the column names of the factor.
- **getTable()**: Returns the table of the factor.
- **eliminate(String variable)**: Eliminates a variable from the factor by summing out the variable.
- **instantiate(String variable, String value)**: Instantiates the factor with a specific value for a variable, reducing its size.
- **join(Factor other)**: Joins this factor with another factor, combining their tables.
- **containsVariable(String variable)**: Checks if the factor contains a specific variable.
- **canBeDiscarded()**: Checks if the factor can be discarded, i.e., if it only has one entry.
- **compareTo(Factor other)**: Compares this factor with another factor based on their column names and sizes.
- **equals(Object o)**: Checks if this factor is equal to another object.
- **hashCode()**: Returns the hash code value for this factor.
- **toString()**: Returns a string representation of this factor.

### VariableElimination.java

- **VariableElimination()**: Constructs a VariableElimination instance with an empty list of factors.
- **initializeFactors(BayesianNetwork network, List<SimpleEntry<String, String>> evidence)**: Initializes the factors of the Bayesian network by creating factors for each node and instantiating them with evidence.
- **runVariableElimination(BayesianNetwork network, List<SimpleEntry<String, String>> evidence, List<String> hiddenVariables)**: Runs the variable elimination algorithm on the Bayesian network.
- **findFactorsWithVariable(String variable)**: Finds the indices of the factors that contain the given variable.
- **getFactors()**: Returns the list of factors currently in the variable elimination process.

### BayesBall.java

- **isIndependent(BayesianNetwork network, String A, String B, Set<String> evidence)**: Determines if two nodes, A and B, are conditionally independent given a set of evidence nodes in a Bayesian network.

### FactorOperationResult.java

- **FactorOperationResult(Factor factor, int multiplications, int additions)**: Constructs a FactorOperationResult with the given factor, multiplications, and additions.
- **getFactor()**: Returns the resulting factor from the operation.
- **getMultiplications()**: Returns the number of multiplications performed during the operation.
- **getAdditions()**: Returns the number of additions performed during the operation.

### Query.java

- **Query(String queryString)**: Constructs a Query from a query string.
- **Query(Query query)**: Constructs a copy of the given Query.
- **getType()**: Returns the type of the query.
- **getQueryVariable()**: Returns the query variable.
- **getQueryValue()**: Returns the query value.
- **removeHiddenVariable(String hiddenVariable)**: Removes a hidden variable from the list of hidden variables.
- **getEvidence()**: Returns the list of evidence for the query.
- **getEvidenceVariables()**: Returns a set of evidence variables (keys only) for the query.
- **getHiddenVariables()**: Returns the list of hidden variables for the query.
- **toString()**: Returns a string representation of the query.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
```

Feel free to customize this README as needed for your project.
