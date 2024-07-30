import React from "react";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      connected: false,
      messages: ''
    };

    this.token = '';
  }

  onTokenChange = (e) => {
    this.token = e.target.value;
    localStorage.setItem('token', e.target.value);
  }

  onMessagesChange = (e) => {
    this.setState(Object.assign(this.state, {messages: e.target.value}));
  }

  connectCallback = (msg) => {
    const userId = msg.headers['user-name'];
    this.stompClient.subscribe(`/user/${userId}/queue/push`, (msg) => {this.onStompMessage(msg)}, {});
    this.setState( Object.assign(this.state, {
      connected: true,
      connectedMessage: `Connected with user ${userId}`
    }));
    console.debug('Connected');
  }

  disconnectClick = () => {
    this.stompClient.disconnect();
  }

  errorCallback = () => {
    console.debug('Error');
  }

  closeEventCallback = () => {
    console.debug('Disconnected');
    this.setState(Object.assign(this.state, {connected: false, connectedMessage: ''}));
  }

  onStompMessage = (msg) => {
    console.log(msg)
    const msgBody = JSON.parse(msg.body);
    const newText = `${this.state.messages}\n\nNew message:\n\tName: ${msgBody.name}\n\tMessage: ${msgBody.message}`;
    this.setState(Object.assign(this.state, {messages: newText}));
    console.debug('Disconnected');
  }

  connectClick = () => {
    this.sockWS = new WebSocket(`ws://localhost:8085/websocketstomp?accessToken=${this.token}`);
    this.stompClient = Stomp.over(this.sockWS);
    this.stompClient.connect({}, this.connectCallback, this.errorCallback, this.closeEventCallback);
  }

  render() {
    return (
      <div>
        <div>
          <textarea onChange = {this.onTokenChange} defaultValue={this.token}></textarea>
        </div>
        <div>
          <button onClick={this.connectClick} disabled={this.state.connected}>connect</button>
          <button onClick={this.disconnectClick} disabled={!this.state.connected}>disconnect</button>
        </div>
        <b/>
        <p/>
        <div>{this.state.connectedMessage}</div>
        <p/>
        <textarea onChange = {this.onMessagesChange} value={this.state.messages} />
      </div>
    );
  }
}

export default App;
