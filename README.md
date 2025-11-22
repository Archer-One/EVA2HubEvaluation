# EVA2HubEvaluation

## Abstract
Off-chain payment channel hubs (PCHs) promise high-throughput cryptocurrency payments via untrusted intermediaries, but existing designs either leak payer–payee relationships or payment values, restrict payments to fixed denominations or one direction, or allow griefing and abort attacks that lock honest users’ funds. We present $\evaahub$, a PCH protocol that simultaneously supports variable-amount payments, two-sided value privacy, relationship anonymity, and robustness to griefing and aborts. $\evaahub$ combines a double-blind signature scheme ($\dbsign$) that authorizes arbitrary payment updates without letting the hub link payments to users, a double-base zero-knowledge range argument ($\dbzkra$) tailored to hidden channel balances, and a new PCH payment mechanism with resistance to aforementioned attacks. We formalize the security of $\evaahub$ in the Universal Composability (UC) framework and prove that it realizes an ideal functionality capturing atomicity, value privacy, balance security, and anonymity against a malicious hub and corrupted users. A prototype implementation on Ethereum shows that $\evaahub$ incurs low computational and communication overhead, making it practical for real-world deployment.

## Testbed
- unbuntu 18.04
- java
- rust
  
# Supplementary Material for Security Analysis
- Security_Proof.pdf
