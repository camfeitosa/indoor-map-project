# Indoor Map

Protótipo de navegação interna para terminais rodoviários. A solução utiliza a planta do terminal como um grafo, calcula a melhor rota com o algoritmo de Dijkstra e simula a atualização da posição do passageiro por beacons Bluetooth.

O Terminal Rodoviário do Tietê é utilizado como cenário da demonstração.

## Funcionalidades

- Seleção de origem e destino dentro do terminal.
- Simulação da leitura de QR Code para identificar a posição inicial.
- Cálculo da menor rota com Dijkstra.
- Exibição da rota sobre a planta do terminal.
- Instruções de direção, como seguir em frente e virar à esquerda ou à direita.
- Zoom automático da rota, zoom manual e movimentação do mapa.
- Beacon Simulator para atualizar a posição durante o percurso.
- Recálculo do caminho restante após cada beacon detectado.
- Funcionamento offline durante a demonstração.

## Demonstração

<p align="center">
  <img src="https://github.com/user-attachments/assets/808fcce5-657e-4e1c-a2e6-293d7d597db1" alt="Tela inicial" width="45%">
  <img src="https://github.com/user-attachments/assets/25647760-bd93-4a0f-9d9e-8407fa57f168" alt="Conteúdo da tela inicial" width="45%">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/b1cfc5be-4072-4259-8d45-9572f1dd3d83" alt="Seleção da localização atual" width="45%">
  <img src="https://github.com/user-attachments/assets/3e4d6132-5d2e-49ed-8a36-1557128e7037" alt="Seleção do destino" width="45%">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/9bb8fb41-fd15-404f-a6a8-e33bebddd645" alt="Leitura do QR Code" width="45%">
  <img src="https://github.com/user-attachments/assets/ffe6ba77-ece3-40c4-810d-59d365326a07" alt="Início da rota" width="45%">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/a140d986-c73e-4143-a007-d7f32a52e3ec" alt="Instruções durante o percurso" width="45%">
  <img src="https://github.com/user-attachments/assets/16da48fe-85b8-47ca-af6f-b110d7b5fe75" alt="Atualização da posição por beacon" width="45%">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/96385d63-ba0c-4a67-b0b6-42c96158d850" alt="Destino alcançado" width="45%">
  <img src="https://github.com/user-attachments/assets/399c941e-4b09-4eeb-bd8c-112ed35a13d8" alt="Busca de destinos" width="45%">
</p>

## Como testar o protótipo

### Opção 1: executar pelo Android Studio

#### Requisitos

- Android Studio.
- JDK 17 ou 21.
- Android SDK 34 ou superior instalado.
- Emulador com Android 8.0 (API 26) ou superior.

#### Passo a passo

1. Clone o repositório:

   ```bash
   git clone https://github.com/camfeitosa/indoor-map-project.git
   ```

2. Entre na pasta do projeto:

   ```bash
   cd indoor-map-project
   ```

3. Abra a pasta do projeto no Android Studio.
4. Aguarde a sincronização do Gradle terminar.
5. Abra o **Device Manager**.
6. Crie ou inicie um emulador com Android 8.0 ou superior.
7. Selecione o emulador na barra superior.
8. Clique em **Run app**.

### Opção 2: executar pelo terminal

Com um emulador aberto, execute:

```bash
./gradlew installDebug
```

## Funcionamento offline

O fluxo principal funciona sem internet porque a planta do terminal, o grafo e o algoritmo de rotas estão incluídos no aplicativo.

A detecção de beacons reais também pode funcionar localmente por Bluetooth. Recursos futuros, como autenticação, atualização remota dos mapas, integração com passagens e análise de uso, poderão exigir conexão com a internet.

## Testes

Para executar os testes unitários:

```bash
./gradlew testDebugUnitTest
```

Os testes cobrem o cálculo da menor rota, pontos desconectados, instruções de direção e a sequência emitida pelo Beacon Simulator.
