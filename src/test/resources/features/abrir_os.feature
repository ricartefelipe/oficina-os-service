# language: pt
Funcionalidade: Abertura de Ordem de Serviço
  Como um atendente da oficina
  Quero abrir uma OS para um cliente
  Para registrar os serviços solicitados

  Cenário: Abrir OS com dados válidos
    Dado um cliente com CPF "123.456.789-09" e veículo com placa "ABC1234"
    Quando o atendente abre uma OS com o serviço "Troca de óleo" por 120.00
    Então a OS é criada com status "RECEBIDA"
    E a OS possui um trackingCode gerado
    E o valor estimado da OS é 120.00

  Cenário: Abertura de OS sem serviços deve falhar
    Dado um cliente com CPF "123.456.789-09" e veículo com placa "ABC1234"
    Quando o atendente tenta abrir uma OS sem nenhum serviço
    Então a operação falha com mensagem "Pelo menos um servico"
