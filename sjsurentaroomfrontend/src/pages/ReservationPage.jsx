
import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import './ReservationPage.css'

const API_URL = import.meta.env.VITE_API_URL

function formatDateTime(isoString) {
    const date = new Date(isoString)
    return date.toLocaleString('en-US', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true,
    })
}

export default function ReservationPage() {
    const { roomId } = useParams()
    const { user } = useAuth()
    const [room, setRoom] = useState(null)
    const [slots, setSlots] = useState([])
    const [selectedSlotId, setSelectedSlotId] = useState(null)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState(null)
    const [submitStatus, setSubmitStatus] = useState(null) 

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [roomRes, availRes] = await Promise.all([
                    fetch(`${API_URL}/rooms/${roomId}`),
                    fetch(`${API_URL}/availability/room/${roomId}`)
                ])
                const roomData = await roomRes.json()
                const availData = await availRes.json()

                setRoom(roomData)
                setSlots(availData.filter(slot => slot.status === 'OPEN'))
            } catch (e) {
                setError(e)
            } finally {
                setIsLoading(false)
            }
        }

        fetchData()
    }, [roomId])

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!selectedSlotId) return

        try {
            const response = await fetch(`${API_URL}/reservations/book`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${user.token}`,
                },
                body: JSON.stringify({
                    roomId: Number(roomId),
                    slotId: selectedSlotId,
                }),
            })

            if (!response.ok) throw new Error('Booking failed')
            setSubmitStatus('success')
        } catch {
            setSubmitStatus('error')
        }
    }

    if (isLoading) return <div className="reservation-status">Loading...</div>
    if (error || !room) return <div className="reservation-status">Could not load room details.</div>

    return (
        <div className="reservation-page">
            <div className="reservation-hero">
                <h1 className="reservation-title">Book {room.name}</h1>
                <p className="reservation-subtitle">{room.building}</p>
            </div>
            <div className="reservation-content">
                <div className="reservation-card">
                    <div className="reservation-card-header">
                        <span className="reservation-room-name">{room.name}</span>
                    </div>
                    <p className="reservation-description">{room.description}</p>
                    <div className="reservation-meta">
                        <span>{room.building}</span>
                        <span>Capacity: {room.capacity}</span>
                    </div>
                </div>

                {submitStatus === 'success' ? (
                    <div className="reservation-success">
                        <span className="reservation-success-icon">&#10003;</span>
                        <div>
                            <strong>Reservation confirmed!</strong>
                            <p>Your time slot has been booked successfully.</p>
                        </div>
                    </div>
                ) : (
                    <form className="reservation-form" onSubmit={handleSubmit}>
                        <h2 className="reservation-form-title">Select a Time Slot</h2>

                        {slots.length === 0 ? (
                            <p className="reservation-no-slots">No available time slots for this room.</p>
                        ) : (
                            <div className="reservation-slots">
                                {slots.map(slot => (
                                    <label
                                        key={slot.slotId}
                                        className={`reservation-slot ${selectedSlotId === slot.slotId ? 'selected' : ''}`}
                                    >
                                        <input
                                            type="radio"
                                            name="slot"
                                            value={slot.slotId}
                                            checked={selectedSlotId === slot.slotId}
                                            onChange={() => setSelectedSlotId(slot.slotId)}
                                        />
                                        <div className="reservation-slot-info">
                                            <span className="reservation-slot-start">{formatDateTime(slot.startTime)}</span>
                                            <span className="reservation-slot-arrow">&#8594;</span>
                                            <span className="reservation-slot-end">{formatDateTime(slot.endTime)}</span>
                                        </div>
                                        <span className="reservation-slot-badge">Open</span>
                                    </label>
                                ))}
                            </div>
                        )}

                        {submitStatus === 'error' && (
                            <p className="reservation-error">Booking failed. Please try again.</p>
                        )}

                        <button
                            type="submit"
                            className="reservation-submit"
                            disabled={!selectedSlotId || slots.length === 0}
                        >
                            Confirm Reservation
                        </button>
                    </form>
                )}
            </div>
        </div>
    )
}
