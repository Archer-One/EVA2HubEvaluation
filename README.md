# EVA2HubEvaluation

## Abstract
Blockchain technology has facilitated the global rise of cryptocurrencies, yet scalability and privacy remain significant challenges to their widespread adoption. The Payment Channel Hub (PCH) is a promising off-chain approach that enables rapid transactions between users via an untrusted intermediary, often referred to as a tumbler. However, existing PCH constructions typically suffer from critical limitations, including privacy leakage, limited support for variable or bidirectional payments, and susceptibility to abort and griefing attacks.

This paper introduces $\evaahub$, a new PCH scheme designed to overcome these challenges. The construction integrates three core components: Double-Blind Signatures ($\dbsign$), a Double-Base Zero-Knowledge Range Argument ($\dbzkra$), and a customized puzzle-based protocol. The $\dbsign$ primitive applies double blinding to messages, allowing privacy-preserving off-chain payments of arbitrary amounts. The $\dbzkra$ enables efficient verification of hidden payment values without revealing them to the tumbler. The tailored puzzle mechanism eliminates the need for conditional payments between the payee and tumbler in the puzzle-promising phase, thereby mitigating abort and griefing risks.

We formally analyze $\evaahub$ within the Universal Composability framework, demonstrating that it achieves atomicity, relationship anonymity, value privacy, and balance security. A prototype implementation on Ethereum shows that $\evaahub$ incurs low computational and communication overhead, making it practical for real-world deployment.

## Testbed
- unbuntu 18.04
- java
- rust
  
