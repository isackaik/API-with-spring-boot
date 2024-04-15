import React,  {useState, useEffect} from 'react';
import { useNavigate, Link, useParams} from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import api from '../../services/Api';

import './styles.css';
import livro from '../../assets/livro.png';


export default function NewBook(){

    const [id, setId] = useState(null);
    const [author, setAuthor] = useState('');
    const [title, setTitle] = useState('');
    const [price, setPrice] = useState('');
    const [launchDate, setLaunchDate] = useState('');

    const accessToken = localStorage.getItem('accessToken');
    const {bookId} = useParams();
    const navigate = useNavigate();

    const header = {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    };

    useEffect(() => {
        if(bookId === '0') return;
        else loadBook();
    }, [bookId]);

    async function loadBook(){
        try {
            const response = await api.get(`api/book/${bookId}`, header);

            let ajustedDate = response.data.launchDate.split('T', 10)[0];

            setId(response.data.id);
            setAuthor(response.data.author);
            setTitle(response.data.title);
            setLaunchDate(ajustedDate);
            setPrice(response.data.price);
        } catch (error) {
            Alert('Error recovering book. Try again!');
            navigate('/books');
        }
    };

    async function saveOrUpdate(e){
        e.preventDefault();

        const data = {
            title, author, launchDate, price
        };

        try {
            if (bookId === '0') {
                await api.post('api/book', data, header);
            } else {
                data.id = id;
                await api.put('api/book', data, header);
            }
            navigate('/books');
        } catch (error) {
            alert('Error while recording Book! Try agains!');
        }
    };

    return (
        <div className="new-book-container">
            <div className="content">
                <section className="form">
                    <img src={livro} alt='Imagem Livro' />
                    <h1>{bookId === '0' ? 'Add new' : 'Update'} Book</h1>
                    <p>Enter the book information and click on {bookId === '0' ? "'Add'" : "'Update'"}!</p>
                    <Link className="back-link" to="/books" >
                        <FiArrowLeft size={16} color='#251FC5' />
                        Back to Books
                    </Link>
                </section>
                <form onSubmit={saveOrUpdate}>
                    <input placeholder='Title' value={title}
                        onChange={e => setTitle(e.target.value)} />
                    <input placeholder='Author' value={author}
                        onChange={e => setAuthor(e.target.value)} />
                    <input type='date' value={launchDate}
                        onChange={e => setLaunchDate(e.target.value)} />
                    <input placeholder='Price' value={price}
                        onChange={e => setPrice(e.target.value)} />

                    <button className="button" type='submit'>{bookId === '0' ? 'Add' : 'Update'}</button>
                </form>
            </div>
        </div>
    );
}