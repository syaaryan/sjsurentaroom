import { Route, Routes } from "react-router-dom"

import Navbar from "./components/Navbar/Navbar"
import ProtectedRoute from "./components/ProtectedRoute"
import { AuthProvider } from "./context/AuthContext"

import Reservations from "./pages/Reservations"
import Rooms from "./pages/Rooms"
import ReservationPage from "./pages/ReservationPage"
import LoginPage from "./pages/LoginPage"
import SignupPage from "./pages/SignupPage"

function App() {
  return (
    <AuthProvider>
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/rooms" element={<Rooms />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/reservations" element={
            <ProtectedRoute><Reservations /></ProtectedRoute>
          } />
          <Route path="/reservations/makereservation/:roomId" element={
            <ProtectedRoute><ReservationPage /></ProtectedRoute>
          } />
        </Routes>
      </div>
    </AuthProvider>
  )
}

export default App
