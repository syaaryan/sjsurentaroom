import { Link, useNavigate } from "react-router-dom"
import { useAuth } from "../../context/AuthContext"
import "./Navbar.css"

export default function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate("/login")
    }

    return (
        <nav className="navbar">
            <h1>SJSU Rent-A-Room</h1>
            <ul>
                <li><Link to="/rooms">Rooms</Link></li>
                {user && <li><Link to="/reservations">My Reservations</Link></li>}
                {user ? (
                    <li>
                        <button onClick={handleLogout} className="navbar-logout-btn">
                            <span className="navbar-logout-name">{user.name}</span>
                            <span className="navbar-logout-divider" />
                            <span className="navbar-logout-label">Log Out</span>
                        </button>
                    </li>
                ) : (
                    <li><Link to="/login" className="navbar-login-btn">Log In</Link></li>
                )}
            </ul>
        </nav>
    )
}
