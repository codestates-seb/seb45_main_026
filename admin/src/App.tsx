import React from 'react';
import './App.css';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import ReportVideoPage from './pages/ReportVideoPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<ReportVideoPage/>}/>
        <Route path='/login' element={<LoginPage/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
