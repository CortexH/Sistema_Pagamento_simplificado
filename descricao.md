Projeto baseado em [Desafio BackEnd - Picpay](https://github.com/PicPay/picpay-desafio-backend)

Framework utilizado:
- Springboot

Ferramenta:
- Maven

Dependências utilizadas:
- Lombok
- security
- h2 database
- Springboot JPA
- jwt

=====================================

Descrição de projeto:

Sistema de pagamento será um serviço / api para realizar transferências.
Será possível realizar dois tipos de transferências: instantânea e agendada.

**Agendada**: O usuário pode colocar um dia, maior que o dia atual, com no máximo
1 mês a mais que o dia atual;
O usuário poderá escolher um horário específico para realizar a transação, que poderá
ser escolhido em intervalos de 30 minutos (o usuário não poderá escolher o horário 12:45, por exemplo.
Também está proibido escolher um horário menor que o horário atual)
**Instantânea**: Acontece de forma instantânea, logo após a transação ser validada.

Usuário:
Teremos 2 tipos de usuários, o **merchant** e o **common**. Merchant funciona como um lojista,
e common como um usuário comum. O merchant não pode realizar transações, apenas recebê-las.
Já o common, poderá realizar e receber transações

Para a criação do usuário, teremos o DTO com os dados:
- primeiro nome
- segundo nome
- senha
- email (único)
- document (ou cpf) (único)
- classificação

Requisitos:

- Tratamento de exceções.
- notificar o usuário sobre o resultado da transação.
- Antes da transação, verificar se existe saldo suficiente.
- Verificar tipo de usuário ao realizar a transação.
- Verificar tempo colocado na transação
- uso de swagger
- autenticação
- login com um usuário


Pontos focais:

- manutenibilidade
- documentação do projeto
- escalabilidade
- inserir autenticação corretamente

=====================================




