
import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import './Rooms.css'

const API_URL = import.meta.env.VITE_API_URL

function Rooms() {
    const [error, setError] = useState()
    const [rooms, setRooms] = useState()
    const [isLoading, setIsLoading] = useState()

    useEffect(() => {
        const fetchRooms = async () => {
            setIsLoading(true)

            try {
                const response = await fetch(`${API_URL}/rooms`)
                const data = await response.json()
                setRooms(data)
            } catch (e) {
                setError(e)
            } finally {
                setIsLoading(false)
            }
        }

        fetchRooms()
    }, [])

    if (isLoading) {
        return <div className="rooms-status">Loading...</div>
    }

    if (error) {
        return <div className="rooms-status">Something went wrong! Please try again.</div>
    }

    return (
        <div className="rooms-page">
            <div className="rooms-hero">
                <h1 className="rooms-title">Available Rooms</h1>
                <p className="rooms-subtitle">Browse and reserve meeting rooms for your team, event, and more!</p>
            </div>
            <div className="rooms-grid">
                {(rooms ?? []).map((room) => (
                    <div className="room-card" key={room.roomId}>
                        <div className="room-card-header">
                            <span className="room-name">{room.name}</span>
                            <span className={`room-badge ${room.availableForBooking ? 'badge-available' : 'badge-unavailable'}`}>
                                {room.availableForBooking ? 'Available' : 'Unavailable'}
                            </span>
                        </div>
                        <p className="room-description">{room.description}</p>
                        <div className="room-meta">
                            <span>{room.building}</span>
                            <span>Capacity: {room.capacity}</span>
                        </div>
                        {room.amenities?.length > 0 && (
                            <div className="room-amenities">
                                {room.amenities}
                            </div>
                        )}
                        {room.availableForBooking ? (
                            <Link
                                to={`/reservations/makereservation/${room.roomId}`}
                                className="book-now-btn"
                            >
                                Book Now
                            </Link>
                        ) : (
                            <span className="book-now-btn book-now-btn--disabled">Book Now</span>
                        )}
                    </div>
                ))}
            </div>
        </div>
    )
}

export default Rooms
