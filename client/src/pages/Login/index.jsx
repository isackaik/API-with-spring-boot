import React, {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import './styles.css';
import api from '../../services/Api';
import livro from '../../assets/livro.png';

export default function Login() {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    async function login(e){
        e.preventDefault();

        const data = {
            username,
            password
        };

        try {
            const response = await api.post('auth/signin', data);
            localStorage.setItem('username', username);
            localStorage.setItem('accessToken', response.data.accessToken);

            navigate('/books');
        } catch (error){
            alert('Login failed. Try agains!')
        }
    }

    return (
        <div className="login-container">
            <section className="form">
                <form onSubmit={login}>
                    <img src={livro} alt="Imagem Livro" />
                    <h1>Bem vindo</h1>
                    <input id="username" placeholder="Username" 
                        value={username} onChange={e => setUsername(e.target.value)} />
                    <input id="password" type="password" placeholder="Password" 
                        value={password} onChange={e => setPassword(e.target.value)} />
                    <button className="button" type="submit">Login</button>
                </form>
            </section>
        </div>
    )
}