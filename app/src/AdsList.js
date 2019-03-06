import React, {Component} from 'react';
import {Row, Col, Button, Container, Table} from 'reactstrap';
import AppNavbar from './AppNavbar';
import ReactLoading from 'react-loading';

class AdsList extends Component {

    constructor(props) {
        super(props);
        this.state = {ads: [], isLoading: true};
        this.compareBy = this.compareBy.bind(this);
        this.sortBy = this.sortBy.bind(this);
        this.downloadAsJson = this.downloadAsJson.bind(this);
    }

    componentDidMount() {
        this.setState({isLoading: true});

        fetch('api/ads')
            .then(response => response.json())
            .then(data => this.setState({ads: data, isLoading: false}));
    }

    compareBy(key) {
        return function (a, b) {
            let parsedA = parseInt(a[key]) || a[key];
            let parsedB = parseInt(b[key]) || b[key];
            if (parsedA < parsedB) return -1;
            if (parsedA > parsedB) return 1;
            return 0;
        };
    }

    sortBy(key) {
        let arrayCopy = [...this.state.ads];
        arrayCopy.sort(this.compareBy(key));
        this.setState({ads: arrayCopy});
    }

    downloadAsJson() {
        const filename = 'ads.json';
        const fileToSave = new Blob([JSON.stringify(this.state.ads)], {
            type: 'application/json',
            name: filename
        });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(fileToSave);
        a.download = filename;
        a.click();
    }

    render() {
        const {ads, isLoading} = this.state;

        if (isLoading) {
            return (
                    <Container>
                        <Row>
                            <Col className={'text-center'}>
                                <h2>Loading</h2>
                                <ReactLoading type={'bars'} color={'#214b56'} height={'80%'} width={'100%'}/>
                            </Col>
                        </Row>
                    </Container>
            );
        }

        const adsList = ads.map(ad => {
            return <tr key={ad.id}>
                <td style={{whiteSpace: 'nowrap'}}>{ad.id}</td>
                <td><a href={ad.link} target="_blank" rel="noopener noreferrer">{ad.title}</a></td>
                <td>{ad.city}</td>
                <td>
                    <img className="img-thumbnail" alt={ad.mainPicture.title} src={ad.mainPicture.link}/>
                </td>
            </tr>
        });

        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <div className="float-right">
                        <Button onClick={() => this.downloadAsJson()} color="success">Download as JSON</Button>
                    </div>
                    <h3>Fetched Data</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th><a onClick={() => this.sortBy('id')}>Id</a></th>
                            <th><a onClick={() => this.sortBy('title')}>Title</a></th>
                            <th><a onClick={() => this.sortBy('city')}>City</a></th>
                            <th>Picture</th>
                        </tr>
                        </thead>
                        <tbody>
                        {adsList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default AdsList;