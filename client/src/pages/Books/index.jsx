import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../services/Api';
import { FiPower, FiEdit, FiTrash2 } from 'react-icons/fi';

import './styles.css';
import livro from '../../assets/livro.png';

export default function Books() {
    const username = localStorage.getItem('username');
    const accessToken = localStorage.getItem('accessToken');
    const navigate = useNavigate();

    const [books, setBooks] = useState([]);
    const [page, setPage] = useState(0);

    const headerWithParams = {
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
        params: {
            page: page,
            size: 4,
            direction: 'desc'
        }
    };

    const headerWithoutParams = {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    };

    async function logout(){
        localStorage.clear();
        navigate('/');
    };

    async function fetchMoreBooks(){
        const response = await api.get('api/book', headerWithParams);
        setBooks([ ...books, ...response.data._embedded.bookVOList]);
        setPage(page + 1);
    }

    async function deleteBooks(id){
        try {
            await api.delete(`api/book/${id}`, headerWithoutParams);

            setBooks(books.filter(book => book.id !== id));
        } catch (error) {
            Alert('Delete failed! Try again!')
        }
    };

    async function editBook(id){
        try {
            navigate(`/book/new/${id}`)
        } catch (error) {
            Alert('Edit book failed! Try again!')
        }
    };

    useEffect(() => {
        fetchMoreBooks();
    }, []);

    return (
        <div className="book-container">
            <header>
                <img src={livro} alt="Imagem Livro" />
                <span>Bem vindo, <strong>{username.toUpperCase()}</strong>!</span>
                <Link className='button' to={"/book/new/0"}>Novo Livro</Link>
                <button type='button' onClick={logout}>
                    <FiPower size={18} color='#251FC5' />
                </button>
            </header>

            <h1>Registered Books</h1>
            <ul>
                {books.map(book => (
                    <li key={book.id}>
                        <strong>Title:</strong>
                        <p>{book.title}</p>
                        <strong>Autor:</strong>
                        <p>{book.author}</p>
                        <strong>Price:</strong>
                        <p>{Intl.NumberFormat('pt-BR', {style: 'currency', currency: 'BRL'}).format(book.price)}</p>
                        <strong>Launch Date:</strong>
                        <p>{Intl.DateTimeFormat('pt-BR').format(new Date(book.launchDate))}</p>

                        <button type='button' onClick={() => editBook(book.id)}>
                            <FiEdit size={20} color="251FC5" />
                        </button>
                        <button type='button' onClick={() => deleteBook(book.id)}>
                            <FiTrash2 size={20} color="251FC5" />
                        </button>
                    </li>
                ))}
            </ul>
            <button className="button" type='button' onClick={fetchMoreBooks}>Load More</button>
        </div>
    );
}