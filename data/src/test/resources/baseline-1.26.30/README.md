# Bedrock 1.26.30 migration baseline

These fixtures are exact copies of `data/resources` files at Allay commit
`cf68fe1ff` (the Minecraft 1.26.30 data update).

`StagedBedrockDataTest` compares the prepared 1.26.50 dataset against this fixed
baseline. Comparing against mutable runtime resources made the migration tests
invalid after the 1.26.40 update and after promoting 1.26.50 data.

The BDS measurement dump is a separate required input. It was not included in
the existing staging commit. These fixtures do not replace or simulate it.
