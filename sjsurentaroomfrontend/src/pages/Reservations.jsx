import { useEffect, useState } from "react"
import { useAuth } from "../context/AuthContext"
import "./Reservations.css"

const API_URL = import.meta.env.VITE_API_URL

function formatDate(iso) {
    return new Date(iso).toLocaleDateString("en-US", {
        weekday: "short", month: "short", day: "numeric", year: "numeric",
    })
}

function formatTime(iso) {
    return new Date(iso).toLocaleTimeString("en-US", {
        hour: "numeric", minute: "2-digit", hour12: true,
    })
}

export default function Reservations() {
    const { user } = useAuth()
    const [reservations, setReservations] = useState([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState(null)
    const [cancelling, setCancelling] = useState(new Set())

    useEffect(() => {
        const fetchReservations = async () => {
            try {
                const res = await fetch(`${API_URL}/reservations/user`, {
                    headers: { "Authorization": `Bearer ${user.token}` },
                })
                if (!res.ok) throw new Error("Failed to load reservations")
                setReservations(await res.json())
            } catch (e) {
                setError(e.message)
            } finally {
                setIsLoading(false)
            }
        }

        fetchReservations()
    }, [user.token])

    const handleCancel = async (reservationId) => {
        setCancelling(prev => new Set(prev).add(reservationId))
        try {
            const res = await fetch(`${API_URL}/reservations/${reservationId}/cancel`, {
                method: "PUT",
                headers: { "Authorization": `Bearer ${user.token}` },
            })
            if (!res.ok) throw new Error()
            setReservations(prev => prev.filter(r => r.reservationId !== reservationId))
        } catch {
            // leave the card as-is, let the user retry
        } finally {
            setCancelling(prev => {
                const next = new Set(prev)
                next.delete(reservationId)
                return next
            })
        }
    }

    if (isLoading) return <div className="reservations-status">Loading your reservations...</div>
    if (error)     return <div className="reservations-status">{error}</div>

    return (
        <div className="reservations-page">
            <div className="reservations-hero">
                <h1 className="reservations-title">My Reservations</h1>
                <p className="reservations-subtitle">View and manage your room bookings</p>
            </div>

            <div className="reservations-content">
                {reservations.length === 0 ? (
                    <div className="reservations-empty">
                        <p>You have no upcoming reservations.</p>
                    </div>
                ) : (
                    <div className="reservations-list">
                        {reservations.map(r => (
                            <div key={r.reservationId} className="res-card">
                                <div className="res-card-header">
                                    <div>
                                        <h2 className="res-room-name">{r.roomName}</h2>
                                    </div>
                                    <span className={`res-status-badge ${r.status.toLowerCase()}`}>
                                        {r.status}
                                    </span>
                                </div>

                                <div className="res-meta">
                                    <span className="res-meta-item">{formatDate(r.startTime)}</span>
                                    <span className="res-meta-item">
                                        {formatTime(r.startTime)} &ndash; {formatTime(r.endTime)}
                                    </span>
                                    <span className="res-meta-item">Capacity: {r.capacity}</span>
                                </div>

                                {r.notes && <p className="res-notes">{r.notes}</p>}

                                {r.status === "CONFIRMED" && (
                                    <>
                                        <div className="res-card-divider" />
                                        <div className="res-card-actions">
                                            <button
                                                className="res-cancel-btn"
                                                onClick={() => handleCancel(r.reservationId)}
                                                disabled={cancelling.has(r.reservationId)}
                                            >
                                                &times; {cancelling.has(r.reservationId) ? "Cancelling..." : "Cancel"}
                                            </button>
                                        </div>
                                    </>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}
