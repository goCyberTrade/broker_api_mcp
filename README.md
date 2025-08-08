<div align="center">

![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/ebtech.png)

MCP Account Asset Query Example：

![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/cursor.gif)

</div>

# IBKR Trading MCP Tools

## Table of contents
- [Overview](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#overview)

- [1.Environment Setup](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#1-environment-setup)

  - [1.1 Install Python Environment](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#11-install-python-environment)
  
  - [1.2 Install UV Tool](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#12-install-uv-tool)
  
  - [1.3 Install JDK](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#13-install-jdk)
  
- [2. Broker MCP Tool Installation & Startup](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#2-broker-mcp-tool-installation--startup)

  - [2.1 IBKR MCP Server Installation](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#21-ibkr-mcp-server-installation)
  
  - [2.2 Start MCP Server](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#22-start-mcp-server)
    - [Option 1: SSE Mode (Recommended)](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#option-1-sse-mode-recommended)
    - [Option 2: STDIO Mode](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#option-2-stdio-mode)
  
    
- [3. Using MCP Tools in Development Environments](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#3-using-mcp-tools-in-development-environments)

  - [3.1 Cursor IDE Integration](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#31-cursor-ide-integration)
  
  - [3.2 VS Code Integration](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/README.md#32-vs-code-integration)



## Overview
This project implements MCP (Market Connectivity Platform) tools for interacting with IBKR (Interactive Brokers) trading systems, including account querying, order placement, position management, and other essential trading functionalities. This project is built on top of Interactive Brokers [WEB API.](https://www.interactivebrokers.com/campus/ibkr-api-page/webapi-doc/#introduction-0)


## 1. Environment Setup

### 1.1 Install JDK
- Download the latest JDK from: [Oracle JDK Downloads](https://www.oracle.com/java/technologies/downloads/#java17)
- Verify installation:
  ```bash
  java -version
  ```


## 2. Broker MCP Tool Installation & Startup

### 2.1 IBKR MCP Server Installation
1. Build the project in the repository root:
   ```bash
   mvn clean install

### 2.2 Start Server
#### Option 1: SSE Mode (Recommended)
```bash
java -jar trade-openapi.jar --api-key 'testKey:XXX'
```
testKey Configuration example:
   ```json
   {
      "tiger": {
          "tigerId": "tiger broker id",
          "account": "tiger broker account",
          "privateKey": "tiger broker privateKey"
      },
      "longport": {
          "appKey": "longport appKey",
          "appSecret": "longport appSecret",
          "accessToken": "longport accessToken"
      },
      "usmart": {
          "phoneNumber": "usmart phoneNumber",
          "password": "usmart password",
          "privateKey": "usmart privateKey",
          "publicKey": "usmart publicKey"
      },
      "futu": {
          "userID": "futu userId",
          "tradePassword": "futu tradePassword",
          "opendIP": "futu OpenD IP address",
          "opendPort": "futu OpenD Port"
      },
      "ibkr": {
          "host": "IB Gateway IP address"
      }
  }
   ```

## 3. Using MCP Tools in Development Environments

### 3.1 Cursor IDE Integration
1. Download Cursor from: [Cursor Official](https://www.cursor.com/cn)
2. Create an MCP Server configuration:
   ```json
   {
     "mcpServers": {
       "tradeMCP": {
         "autoApprove": [],
         "disabled": false,
         "timeout": 60,
         "type": "sse",
         "url": "http://127.0.0.1:8000/sse?channel=ibkr&apiKey=testKey"
       }
     }
   }
   ```
3. Enable the configuration and ensure status is green.
   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/cursor_tools.png)
4. Open the chat interface (`Ctrl+L` by default) to use the tools.

   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/cursor.gif)

### 3.2 VS Code Integration
1. Download VS Code from: [VS Code Official](https://code.visualstudio.com/)
2. Install the "Cline" extension from the Marketplace.
   
   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/vs_cline.png)
4. Configure your AI model API key (e.g., DeepSeek).
   
   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/vs_model.png)
6. Configure MCP service with:
   ```json
   {
     "mcpServers": {
       "tradeMCP": {
         "autoApprove": [],
         "disabled": false,
         "timeout": 60,
         "type": "sse",
         "url": "http://127.0.0.1:8000/sse?channel=ibkr&apiKey=testKey"
       }
     }
   }
   ```
7. Enable the configuration and ensure status is green.
   
   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/vs_tools.png)
9. Use the chat interface for tool interactions.
    
   ![image](https://github.com/goCyberTrade/ibkr_trade_mcp/blob/main/pics/vs_test.gif)
11. Optionally enable "Auto-approve" to skip confirmation for future tool calls.

