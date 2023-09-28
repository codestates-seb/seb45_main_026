import React from 'react';
import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import ReportVideoPage from './pages/reports/ReportVideoPage';
import Header from './components/header/Header';

function App() {
  return (
    <BrowserRouter>
      <Header/>
      <Routes>
        <Route path='/' element={<ReportVideoPage/>}/>
        <Route path='/login' element={<LoginPage/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
