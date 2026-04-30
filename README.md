# Monopoly Game

A real-time multiplayer Monopoly board game built with modern web technologies. Players can create games, join waiting rooms, and play the classic Monopoly game with features like property buying, auctions, trading, and more.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [Running the Application](#running-the-application)
- [Game Mechanics](#game-mechanics)
- [API Endpoints](#api-endpoints)
- [WebSocket Events](#websocket-events)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Real-time Multiplayer**: WebSocket-based communication for live game updates
- **Game Creation and Joining**: Create new games or join existing ones via waiting rooms
- **Classic Monopoly Gameplay**: Property buying, rent collection, auctions, trading
- **Interactive 3D Board**: Three.js-powered 3D game board visualization
- **Admin Panel**: Game administrators can manage game settings and players
- **Responsive UI**: Modern React-based interface with multiple game panels
- **Database Persistence**: MySQL database for game state management
- **Development Mode**: Special mode for testing with bot players

## Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Web**: For RESTful API endpoints
- **Spring Data JPA**: ORM for database interactions
- **Spring WebSocket**: Real-time communication
- **MySQL**: Relational database for game persistence
- **Maven**: Build and dependency management

### Frontend
- **React 19**
- **React Router**
- **STOMP.js**: WebSocket client for real-time messaging
- **SockJS**: WebSocket fallback for older browsers
- **Three.js**: 3D graphics library for board visualization
- **React Testing Library**: Testing utilities

### Development Tools
- **JUnit**: Backend testing
- **npm**: Frontend package management

## Architecture

The application follows a client-server architecture with real-time communication:

### Backend Architecture
- **Controllers**: Handle HTTP requests 
- **Services**: Business logic 
- **Models**: Data entities (Game, PlayerState, TileState, etc.)
- **Repository**: Data access layer 
- **WebSocket**: Real-time game updates

### Frontend Architecture
- **Components**: Reusable UI components (BoardComponent, AuctionPrompt, etc.)
- **Hooks**: Custom React hooks for state management and WebSocket communication
- **Scenes**: Main application views (MainMenu, WaitingRoom, Scene)
- **Services**: WebSocket communication layer


## Running the Application

1. **Start the Backend**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   The backend will start on `http://localhost:8080`

2. **Start the Frontend**:
   ```bash
   cd frontend
   npm start
   ```
   The frontend will start on `http://localhost:3000`

3. **Access the Application**:
   Open `http://localhost:3000` in your browser

## Game Mechanics

### Basic Gameplay
- Players roll dice to move around the board
- Land on properties to buy them or pay rent
- Collect salary when passing GO
- Engage in auctions for unowned properties
- Trade properties with other players
- Pay taxes and fees
- Go to jail and try to get out

### Special Features
- **Auction System**: Unowned properties go to auction
- **Trading System**: Players can trade properties and money
- **Community Chest/Chance Cards**: Random events (partially implemented)
- **Jail Mechanics**: Players can pay, use cards, or roll doubles to escape
- **Bankruptcy**: Players can go bankrupt and be eliminated

### Game States
- **Waiting Room**: Players join before game starts
- **Active Game**: Main gameplay phase
- **Post-Move**: Actions after moving (buy property, pay rent, etc.)
- **Auction**: Property auction phase
- **Trading**: Player-to-player trading

## API Endpoints

### Game Management
- `POST /games/createGame` - Create a new game
- `POST /games/joinGame` - Join an existing game
- `GET /games/{gameId}` - Get game state
- `POST /games/{gameId}/start` - Start the game

### Player Actions
- `POST /games/{gameId}/rollDice` - Roll dice for current player
- `POST /games/{gameId}/buyProperty` - Buy the property player landed on
- `POST /games/{gameId}/auctionProperty` - Start auction for property
- `POST /games/{gameId}/trade` - Initiate trade with another player

## WebSocket Events

The application uses STOMP over WebSocket for real-time communication:

### Topics
- `/topic/game/{gameId}` - Game state updates
- `/topic/game/{gameId}/players` - Player updates
- `/topic/game/{gameId}/tiles` - Tile state updates

### Client Destinations
- `/app/game/{gameId}/join` - Join game WebSocket session
- `/app/game/{gameId}/action` - Send player actions

## Future Improvements

As this is a work-in-progress (WIP) project, here are planned enhancements:

### High Priority
- **Complete Card System**: Implement all Community Chest and Chance cards
- **Jail Mechanics**: Full implementation of jail rules and get-out-of-jail cards
- **Bankruptcy Handling**: Proper bankruptcy logic when players can't pay debts
- **Game End Conditions**: Win conditions and game completion
- **Error Handling**: Better error messages and recovery mechanisms

### Medium Priority
- **AI Bots**: Computer-controlled players for single-player mode
- **Game Persistence**: Save/load game states across sessions
- **Spectator Mode**: Allow players to watch ongoing games
- **Chat System**: In-game text chat between players
- **Game Statistics**: Track wins, losses, and player statistics

### Low Priority / Enhancements
- **Mobile Responsiveness**: Optimize UI for mobile devices
- **Themes**: Multiple board themes and customization options
- **Sound Effects**: Audio feedback for game events
- **Animations**: Smooth transitions and animations for game actions
- **Multi-language Support**: Localization for different languages
- **Advanced Trading**: More complex trade proposals and negotiations

### Technical Improvements
- **Unit Tests**: Comprehensive test coverage for both backend and frontend
- **Integration Tests**: End-to-end testing for game flows
- **Performance Optimization**: Optimize WebSocket communication and rendering
- **Security**: Authentication and authorization for games
- **Docker Support**: Containerization for easy deployment
- **CI/CD Pipeline**: Automated testing and deployment

### UI/UX Improvements
- **Better 3D Graphics**: Enhanced board and piece visualization
- **Accessibility**: Screen reader support and keyboard navigation
- **Tutorial Mode**: Guided tutorial for new players
- **Game History**: Review past moves and game events

## License

This project is licensed under the MIT License - see the LICENSE file for details.
