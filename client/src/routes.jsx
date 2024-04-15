import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Books from './pages/Books';
import Login from './pages/Login';
import NewBook from './pages/NewBook';

export default function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Login />}/>
                <Route path="/books" element={<Books />}/>
                <Route path="/book/new/:bookId" element={<NewBook />}/>
            </Routes>
        </BrowserRouter>
    );
}