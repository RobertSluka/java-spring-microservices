// src/components/services/doctor-service/doctor-filter.tsx
import React, { useEffect, useMemo, useState } from "react";
import { Alert, Button, Col, Form, InputGroup, Row, Spinner, Table } from "react-bootstrap";
import api from "../../../api/axiosConfig";
import "./doctor-filter.css";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { InfoCircle } from "react-bootstrap-icons"; // or use any icon you like

type Patient = {
    id: string;                 // UUID
    name: string;
    email?: string;
    dateOfBirth?: string;       // ISO (YYYY-MM-DD)
    // add more fields if your DTO has them
};

type SortKey = "" | "name" | "dob";

const DoctorFilter: React.FC = () => {
    const [patients, setPatients] = useState<Patient[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [filterName, setFilterName] = useState("");
    const [dob, setDob] = useState<string>("");          // YYYY-MM-DD from <input type="date">
    const [sortBy, setSortBy] = useState<SortKey>("");

    const [idQuery, setIdQuery] = useState("");

    const debouncedName = useDebounce(filterName, 300);

    useEffect(() => {
        loadAllPatients();
    }, []);

    useEffect(() => {
        if (!debouncedName && !dob) {
            if (!sortBy) {
                loadAllPatients();
            } else {
                fetchSorted(sortBy);
            }
            return;
        }
        fetchFiltered(debouncedName, dob);
    }, [debouncedName, dob]);

    // re-run when sort changes
    useEffect(() => {
        if (!sortBy) return;
        fetchSorted(sortBy);
    }, [sortBy]);

    async function loadAllPatients() {
        try {
            setLoading(true);
            setError(null);
            const { data } = await api.get<Patient[]>("/patients");
            setPatients(Array.isArray(data) ? data : []);
        } catch (e: any) {
            setError(e?.response?.data?.message ?? "Failed to load patients.");
        } finally {
            setLoading(false);
        }
    }

    async function fetchFiltered(name: string, date: string) {
        try {
            setLoading(true);
            setError(null);
            const params: Record<string, string> = {};
            if (name) params.filterName = name;
            if (date) params.dateOfBirth = date; // controller expects ISO date

            const { data } = await api.get<Patient[]>("/patients/filter", { params });
            setPatients(Array.isArray(data) ? data : []);
        } catch (e: any) {
            setError(e?.response?.data?.message ?? "Failed to filter patients.");
        } finally {
            setLoading(false);
        }
    }

    async function fetchSorted(key: SortKey) {
        if (!key) return;
        try {
            setLoading(true);
            setError(null);
            const { data } = await api.get<Patient[]>("/patients/sort", {
                params: { sortBy: key },
                headers: { 'Cache-Control': 'no-cache' },
            });
            setPatients(Array.isArray(data) ? data : []);
        } catch (e: any) {
            setError(e?.response?.data?.message ?? "Failed to sort patients.");
        } finally {
            setLoading(false);
        }
    }

    async function fetchById() {
        if (!idQuery.trim()) return;
        try {
            setLoading(true);
            setError(null);
            const { data } = await api.get<Patient>("/patients/id", {
                params: { id: idQuery.trim() },
            });
            setPatients(Array.isArray(data) ? data : []);
        } catch (e: any) {
            setError(e?.response?.data?.message ?? "Patient not found.");
            setPatients([]);
        } finally {
            setLoading(false);
        }
    }

    function clearAll() {
        setFilterName("");
        setDob("");
        setSortBy("");
        setIdQuery("");
        loadAllPatients();
    }

    const hasFilters = useMemo(
        () => Boolean(filterName || dob || sortBy || idQuery),
        [filterName, dob, sortBy, idQuery]
    );

    return (
        <div className="doctor-filter">
            <h3 className="mb-3">Patient Search (Doctor)</h3>

            <Row className="g-3 align-items-end">
                <Col md={4}>
                    <Form.Label>Filter by Name and Date of Birth</Form.Label>
                    <Form.Control
                        placeholder="e.g. John, Jane, Smith..."
                        value={filterName}
                        onChange={(e) => setFilterName(e.target.value)}
                    />
                </Col>

                <Col md={3}>
                    <Form.Label className="d-inline-flex align-items-center gap-1">
                        Born before
                        <OverlayTrigger
                            placement="right"
                            delay={{ show: 150, hide: 100 }}
                            overlay={
                                <Tooltip id="dob-tooltip">
                                    Returns patients whose <strong>dateOfBirth ≤ selected date</strong>.
                                </Tooltip>
                            }
                        >
      <span
          role="button"
          tabIndex={0}
          aria-label="Help: born before filter"
          className="text-muted"
          style={{ lineHeight: 1 }}
      >
        <InfoCircle size={16} color="white" />
      </span>
                        </OverlayTrigger>
                    </Form.Label>

                    <Form.Control
                        type="date"
                        value={dob}
                        onChange={(e) => setDob(e.target.value)}
                    />
                </Col>

                <Col md={3}>
                    <Form.Label>Sort By</Form.Label>
                    <Form.Select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value as SortKey)}
                    >
                        <option value="">— None —</option>
                        <option value="name">Name</option>
                        <option value="dob">Date of Birth</option>
                    </Form.Select>
                </Col>

                {/* Reset button stays on the right */}
                <Col md={2} className="d-flex gap-2">
                    <Button variant="secondary" onClick={clearAll}>
                        Reset
                    </Button>
                </Col>
            </Row>

            <Row className="g-3 align-items-end mt-2">
                <Col md={5}>
                    <Form.Label>Find by ID (UUID)</Form.Label>
                    <InputGroup>
                        <Form.Control
                            placeholder="UUID..."
                            value={idQuery}
                            onChange={(e) => setIdQuery(e.target.value)}
                        />
                        <Button variant="outline-primary" onClick={fetchById}>
                            Fetch
                        </Button>
                    </InputGroup>
                </Col>
            </Row>

            <div className="mt-4">
                {error && <Alert variant="danger">{error}</Alert>}
                {loading ? (
                    <div className="d-flex align-items-center gap-2">
                        <Spinner animation="border" size="sm" /> Loading...
                    </div>
                ) : (
                    <Table striped bordered hover responsive size="sm">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Date of Birth</th>
                        </tr>
                        </thead>
                        <tbody>
                        {patients.length === 0 ? (
                            <tr>
                                <td colSpan={5} className="text-center">
                                    {hasFilters ? "No results for current filters." : "No patients available."}
                                </td>
                            </tr>
                        ) : (
                            patients.map((p, idx) => (
                                <tr key={p.id}>
                                    <td style={{color: idx ? "black" : "red"}}>
                                        {idx + 1}</td>
                                    <td><code style={{color: p.id ? "black" : "red", fontSize: 12}}>{p.id}</code></td>
                                    <td style={{color: p.name ? "black" : "red"}}>
                                        {p.name ?? "-"}</td>
                                    <td style={{color: p.email ? "black" : "red"}}>
                                        {p.email ?? "—"}
                                    </td>
                                    <td style={{color: p.dateOfBirth ? "black" : "red"}}>{p.dateOfBirth ?? "—"}</td>

                                </tr>
                            ))
                        )}
                        </tbody>
                    </Table>
                )}
            </div>
        </div>
    );
};

export default DoctorFilter;

/** Small debounce hook */
function useDebounce<T>(value: T, delay = 250): T {
    const [debounced, setDebounced] = useState(value);
    useEffect(() => {
        const id = setTimeout(() => setDebounced(value), delay);
        return () => clearTimeout(id);
    }, [value, delay]);
    return debounced;
}