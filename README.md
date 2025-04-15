# EVA2HubEvaluation

## Abstract
Blockchain technology has driven the global adoption of cryptocurrencies, but its scalability and privacy limitations remain major obstacles to wider deployment. The Payment Channel Hub (PCH) offers a promising solution by enabling fast, off-chain payments between users through an untrusted intermediary (referred to as a tumbler). However, existing PCH designs often face one or more limitations: payment privacy leakage, lack of support for variable or bidirectional payments, and vulnerability to abort or griefing attacks.

To address these issues, we propose a new PCH scheme, $\evaahub$, built on three core components: Double-Blind Signatures ($\dbsign$), a Double-Base Zero-Knowledge Range Argument ($\dbzkra$), and a customized puzzle-based mechanism. The $\dbsign$ introduces a new primitive that applies double blinding to messages, enabling off-chain payments of variable amounts while preserving privacy. The $\dbzkra$ proves the correctness of blinded amounts to the intermediary without compromising efficiency. The tailored puzzle mechanism removes the conditional transaction between the payee and tumbler in the promising phase, effectively preventing abort and griefing attacks.

We formally analyze the security of $\evaahub$ in the Universal Composability framework and prove that it ensures atomicity, relationship anonymity, value privacy, and balance security. We also implement a prototype on Ethereum and evaluate its performance. Results show that $\evaahub$ is highly efficient in both communication and computation.

## Testbed
- unbuntu 18.04
- java
- rust
  
